/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015-2021 SonarSource
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
package org.sonarlint.intellij.clion;

import javax.annotation.Nullable;

public class BuildWrapperJsonGenerator {
  private final StringBuilder builder;
  private boolean first = true;

  public BuildWrapperJsonGenerator() {
    builder = new StringBuilder()
      .append("{"
        + "\"version\":0,"
        + "\"captures\":[");
  }

  public BuildWrapperJsonGenerator add(AnalyzerConfiguration.Configuration configuration) {
    if (first) {
      first = false;
    } else {
      builder.append(",");
    }
    if (configuration.remoteToolchain) {
      appendRemoteEntry(configuration);
    } else {
      appendEntry(configuration);
    }
    return this;
  }

  private void appendRemoteEntry(AnalyzerConfiguration.Configuration entry) {
    String quotedFilePath = quote(entry.virtualFile.getCanonicalPath());
    String stdout = entry.predefinedMacros + "\n";
    StringBuilder stdErrBuilder = new StringBuilder("#include <...> search starts here:\n");
    entry.includes.forEach(i -> stdErrBuilder.append(" ").append(i).append("\n"));
    stdErrBuilder.append("End of search list.\n");
    String stderr = stdErrBuilder.toString();

    addProbe(entry, quotedFilePath, stdout, stderr);
    addProbe(entry, quotedFilePath, stdout, stderr);
    builder.append("{")
      .append("\"compiler\":\"")
      .append(entry.compilerKind)
      .append("\",")
      .append("\"cwd\":" + quote(entry.compilerWorkingDir) + ",")
      .append("\"executable\":")
      .append(quotedFilePath)
      .append(",");
    if (entry.isHeaderFile) {
      builder.append("\"properties\":{\"isHeaderFile\":\"true\"},");
    }
    builder.append("\"cmd\":[")
      .append(quote(entry.compilerExecutable))
      .append("," + quotedFilePath);
    builder.append("]}");
  }

  private void addProbe(AnalyzerConfiguration.Configuration entry, String quotedFilePath, String stdout, String stderr) {
    builder
      .append("{")
      .append("\"compiler\":\"").append(entry.compilerKind).append("\",")
      .append("\"executable\":").append(quotedFilePath).append(",")
      .append("\"stdout\":").append(quote(stdout)).append(",")
      .append("\"stderr\":").append(quote(stderr))
      .append("},");
  }

  private void appendEntry(AnalyzerConfiguration.Configuration entry) {
    String quotedCompilerExecutable = quote(entry.compilerExecutable);
    builder.append("{")
      .append("\"compiler\":\"")
      .append(entry.compilerKind)
      .append("\",")
      .append("\"cwd\":" + quote(entry.compilerWorkingDir) + ",")
      .append("\"executable\":")
      .append(quotedCompilerExecutable)
      .append(",");
    if (entry.isHeaderFile) {
      builder.append("\"properties\":{\"isHeaderFile\":\"true\"},");
    }
    builder.append("\"cmd\":[")
      .append(quotedCompilerExecutable)
      .append("," + quote(entry.virtualFile.getCanonicalPath()));
    entry.compilerSwitches.forEach(s -> builder.append(",").append(quote(s)));
    builder.append("]}");
  }

  public String build() {
    return builder.append("]}").toString();
  }

  static String quote(@Nullable String string) {
    if (string == null || string.length() == 0) {
      return "\"\"";
    }

    char c;
    int i;
    int len = string.length();
    StringBuilder sb = new StringBuilder(len + 4);
    String t;

    sb.append('"');
    for (i = 0; i < len; i += 1) {
      c = string.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ') {
            t = "000" + Integer.toHexString(c);
            sb.append("\\u" + t.substring(t.length() - 4));
          } else {
            sb.append(c);
          }
      }
    }
    sb.append('"');
    return sb.toString();
  }
}
