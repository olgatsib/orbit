/*
 Copyright (C) 2015 - 2019 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <https://www.orbit.cloud>.
 See license in LICENSE.
 */

package cloud.orbit.runtime.remoting

import cloud.orbit.core.remoting.AddressableClass
import java.lang.reflect.Method

data class RemoteInterfaceDefinition(
    val interfaceClass: AddressableClass,
    val methodDefinitions: Map<Method, RemoteMethodDefinition>
)

data class RemoteMethodDefinition(
    val method: Method
)