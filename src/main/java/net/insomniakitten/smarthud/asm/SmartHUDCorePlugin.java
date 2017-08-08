package net.insomniakitten.smarthud.asm;

/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("Smart HUD Core")
@IFMLLoadingPlugin.MCVersion("%mc_version%")
@IFMLLoadingPlugin.TransformerExclusions("net.insomniakitten.smarthud.asm")
public class SmartHUDCorePlugin implements IFMLLoadingPlugin {

    public static final String CLASS_TRANSFORMER = "net.insomniakitten.smarthud.asm.SmartHUDTransformer";

    public static boolean runtimeDeobf = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ CLASS_TRANSFORMER };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
