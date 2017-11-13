package net.insomniakitten.smarthud.feature; 
 
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

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;

public final class FeatureRegistryEvent extends Event {

    private final List<ISmartHUDFeature> hudFeatures = new ArrayList<>();

    public FeatureRegistryEvent() {}

    public void register(ISmartHUDFeature feature) {
        hudFeatures.add(feature);
    }

    public ImmutableList<ISmartHUDFeature> getFeatures() {
        return ImmutableList.copyOf(hudFeatures);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

}
