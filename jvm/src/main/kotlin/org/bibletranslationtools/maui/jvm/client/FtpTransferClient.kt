package org.bibletranslationtools.maui.jvm.client

import io.reactivex.Completable
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.bibletranslationtools.maui.common.client.IFileTransferClient
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.SocketException

class FtpTransferClient(
    private val source: File,
    private val targetPath: String,
    private val ftpServer: String,
    private val ftpUser: String,
    private val ftpPassword: String
) : IFileTransferClient {

    override fun transfer(): Completable {
        return Completable.fromCallable {
            if (source.isDirectory) throw IllegalArgumentException("Source should not be a directory")

            val ftpClient = FTPClient()
            try {
                ftpClient.connect(ftpServer)
                ftpClient.login(ftpUser, ftpPassword)
                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                    ftpClient.disconnect()
                    throw IOException("Username or password incorrect.")
                }

                val dirsCreated = createFtpDirectories(ftpClient)
                if (dirsCreated) {
                    source.inputStream().use {
                        if (!ftpClient.storeFile(targetPath, it)) {
                            throw IOException("Transfer of ${source.name} failed!")
                        }
                    }
                } else {
                    throw IOException("Could not create $targetPath on the server!")
                }
            } catch (e: IOException) {
                throw IOException(e.message)
            } catch (e: SocketException) {
                throw SocketException(e.message)
            } finally {
                if (ftpClient.isConnected) {
                    ftpClient.logout()
                    ftpClient.disconnect()
                }
            }
        }
    }

    private fun createFtpDirectories(ftpClient: FTPClient): Boolean {
        val pathElements = targetPath.split("/")
        for (dir in pathElements) {
            val existed = ftpClient.changeWorkingDirectory(dir)
            // Do not create a dir if it looks like a file name
            if (!existed && !dir.contains(".")) {
                val created = ftpClient.makeDirectory(dir)
                if (created) {
                    ftpClient.changeWorkingDirectory(dir)
                } else {
                    return false
                }
            }
        }
        // Reset working directory to root directory
        ftpClient.changeWorkingDirectory("/")
        return true
    }
}
