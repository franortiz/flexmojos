<%--
  GRANITE DATA SERVICES
  Copyright (C) 2007-2008 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  @author Franck WOLFF
--%><%
    Set as3Imports = new TreeSet();

    as3Imports.add("flash.utils.IDataInput");
    as3Imports.add("flash.utils.IDataOutput");

    if (jClass.hasEnumProperty())
        as3Imports.add("org.granite.util.Enum");

    for (jImport in jClass.imports) {
        if (jImport.as3Type.hasPackage() && jImport.as3Type.packageName != jClass.as3Type.packageName)
            as3Imports.add(jImport.as3Type.qualifiedName);
    }

%>/**
 * Generated by Gas3 v${gVersion} (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERRIDDEN EACH TIME YOU USE
 * THE GENERATOR. CHANGE INSTEAD THE INHERITED CLASS (${jClass.as3Type.name}.as).
 */

package ${jClass.as3Type.packageName} {
<%
///////////////////////////////////////////////////////////////////////////////
// Write Import Statements.

    for (as3Import in as3Imports) {%>
    import ${as3Import};<%
    }

///////////////////////////////////////////////////////////////////////////////
// Write Class Declaration.%>

    public class ${jClass.as3Type.name}Base<%

        boolean implementsWritten = false;
        if (jClass.hasSuperclass()) {
            %> extends ${jClass.superclass.as3Type.name}<%
        }

        for (jInterface in jClass.interfaces) {
            if (!implementsWritten) {
                %> implements ${jInterface.as3Type.name}<%

                implementsWritten = true;
            } else {
                %>, ${jInterface.as3Type.name}<%
            }
        }

    %> {
<%

    ///////////////////////////////////////////////////////////////////////////
    // Write Private Fields.

    for (jProperty in jClass.properties) {%>
        private var _${jProperty.name}:${jProperty.as3Type.name};<%
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write Public Getter/Setter.

    for (jProperty in jClass.properties) {
        if (jProperty.readable || jProperty.writable) {%>
<%
            if (jProperty.writable) {%>
        public function set ${jProperty.name}(value:${jProperty.as3Type.name}):void {
            _${jProperty.name} = value;
        }<%
            }
            if (jProperty.readable) {%>
        public function get ${jProperty.name}():${jProperty.as3Type.name} {
            return _${jProperty.name};
        }<%
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write Public Getters/Setters for Implemented Interfaces.

    if (jClass.hasInterfaces()) {
        for (jProperty in jClass.interfacesProperties) {
            if (jProperty.readable || jProperty.writable) {%>
<%
                if (jProperty.writable) {%>
        public function set ${jProperty.name}(value:${jProperty.as3Type.name}):void {
        }<%
                }
                if (jProperty.readable) {%>
        public function get ${jProperty.name}():${jProperty.as3Type.name} {
            return ${jProperty.as3Type.nullValue};
        }<%
                }
            }
        }
    }
%>

    }
}