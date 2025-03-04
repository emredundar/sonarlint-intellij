/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015-2022 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.rider

import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import org.sonarlint.intellij.common.analysis.AnalysisConfigurator
import org.sonarlint.intellij.common.analysis.AnalysisConfigurator.AnalysisConfiguration

class RiderAnalysisConfigurator : AnalysisConfigurator {
    override fun configure(module: Module, filesToAnalyze: Collection<VirtualFile>): AnalysisConfiguration {
        val result = AnalysisConfiguration()
        val dotNetCoreRuntime = RiderDotNetActiveRuntimeHost.getInstance(module.project).dotNetCoreRuntime.value
        if (dotNetCoreRuntime != null) {
            result.extraProperties["sonar.cs.internal.dotnetCliExeLocation"] = dotNetCoreRuntime.cliExePath
        }
        val monoRuntime = RiderDotNetActiveRuntimeHost.getInstance(module.project).monoRuntime
        if (monoRuntime != null) {
            result.extraProperties["sonar.cs.internal.monoExeLocation"] = monoRuntime.getMonoExe().absolutePath
        }
        return result
    }
}
