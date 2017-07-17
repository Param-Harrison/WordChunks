/*
 * Copyright 2017 Julia Kozhukhovskaya
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

package com.appchamp.wordchunks.realmdb.firebaseDao

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

/**
 * Note that some inline functions may call the lambdas passed to them as parameters not directly
 * from the function body, but from another execution context, such as a local object or a nested
 * function. In such cases, non-local control flow is also not allowed in the lambdas. To indicate
 * that, the lambda parameter needs to be marked with the crossinline modifier:
 */
inline fun <reified T : Any> DatabaseReference.fetchFirebaseList(
        crossinline call: (MutableList<T>) -> Unit, crossinline callError: (String) -> Unit,
        childLang: String, childName: String) {

    val TAG = javaClass.simpleName

    this.child(childLang)
            .child(childName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val list = mutableListOf<T>()
                    dataSnapshot?.children?.map {
                        Log.d(TAG, it.toString())
                        val item = it.getValue(T::class.java)
                        item?.let { list.add(it) }
                    }
                    call(list)
                }

                override fun onCancelled(error: DatabaseError?) {
                    callError(error.toString())
                }
            })
}