/**
 * Copyright 2010 - 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.exodus.log.replication

import jetbrains.exodus.env.replication.ReplicationDelta
import org.junit.Assert
import org.junit.Test

class EmptyLogReplicationTest : ReplicationBaseTest() {

    @Test
    fun `should append changes in one file`() {
        var (sourceLog, targetLog) = newLogs()

        val count = 10
        writeToLog(sourceLog, count)
        Assert.assertEquals(1, sourceLog.allFileAddresses.size)

        targetLog.appendLog(
                ReplicationDelta(1, 0, sourceLog.highAddress, sourceLog.fileSize, sourceLog.allFileAddresses)
        )

        sourceLog.close()

        // check log with cache
        checkLog(targetLog, count)

        targetLog = targetLogDir.createLog(fileSize = 4L) {
            cachePageSize = 1024
        }

        // check log without cache
        checkLog(targetLog, count)
    }

    fun `should append changes in few files`() {
        var (sourceLog, targetLog) = newLogs()

        val count = 1000
        writeToLog(sourceLog, count)

        Assert.assertTrue(sourceLog.allFileAddresses.size > 1)

        targetLog.appendLog(
                ReplicationDelta(1, 0, sourceLog.highAddress, sourceLog.fileSize, sourceLog.allFileAddresses)
        )

        sourceLog.close()

        // check log with cache
        checkLog(targetLog, count)

        targetLog = targetLogDir.createLog(fileSize = 4L) {
            cachePageSize = 1024
        }

        // check log without cache
        checkLog(targetLog, count)
    }

}