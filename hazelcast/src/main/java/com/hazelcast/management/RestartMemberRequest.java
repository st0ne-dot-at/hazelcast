/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.management;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;

/**
 * User: sancar
 * Date: 3/15/13
 * Time: 11:24 AM
 */
public class RestartMemberRequest implements ConsoleRequest {
    public int getType() {
        return ConsoleRequestConstants.REQUEST_TYPE_MEMBER_RESTART;
    }

    public Object readResponse(ObjectDataInput in) throws IOException {
        return in.readUTF();
    }

    public void writeResponse(ManagementCenterService mcs, ObjectDataOutput dos) throws Exception {
//        mcs.getHazelcastInstance().getLifecycleService().restart(); //TODO @msk restart ???
        dos.writeUTF("successful");
    }

    public void writeData(ObjectDataOutput out) throws IOException {
    }

    public void readData(ObjectDataInput in) throws IOException {
    }
}
