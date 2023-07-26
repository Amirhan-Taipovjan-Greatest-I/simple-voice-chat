package de.maxhenkel.voicechat.resourcepacks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.maxhenkel.voicechat.Voicechat;
import net.minecraft.resources.*;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VoiceChatResourcePack extends ResourcePack {

    protected String path;
    protected TextComponent name;

    public VoiceChatResourcePack(String path, TextComponent name) {
        super(null);
        this.path = path;
        this.name = name;
    }

    @Nullable
    public ResourcePackInfo toPack() {
        try {
            PackMetadataSection packMetadataSection = getMetadataSection(PackMetadataSection.SERIALIZER);
            if (packMetadataSection == null) {
                return null;
            }
            return new ResourcePackInfo(path, false, () -> this, name, packMetadataSection.getDescription(), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, false, IPackNameDecorator.BUILT_IN);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return path;
    }

    private String getPath() {
        return "/packs/" + path + "/";
    }

    @Nullable
    private InputStream get(String name) {
        return Voicechat.class.getResourceAsStream(getPath() + name);
    }

    @Override
    protected InputStream getResource(String name) throws IOException {
        InputStream resourceAsStream = get(name);
        if (resourceAsStream == null) {
            throw new FileNotFoundException("Resource " + name + " does not exist");
        }
        return resourceAsStream;
    }

    @Override
    protected boolean hasResource(String name) {
        try {
            return get(name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(ResourcePackType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        List<ResourceLocation> list = Lists.newArrayList();
        try {
            URL url = Voicechat.class.getResource(getPath());
            Path namespacePath = Paths.get(url.toURI()).resolve(type.getDirectory()).resolve(namespace);
            Path resPath = namespacePath.resolve(prefix);

            if (!Files.exists(resPath)) {
                return list;
            }

            try (Stream<Path> files = Files.walk(resPath)) {
                files.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                    ResourceLocation resourceLocation = new ResourceLocation(namespace, convertPath(path).substring(convertPath(namespacePath).length() + 1));
                    list.add(resourceLocation);
                });
            }
        } catch (Exception e) {
            Voicechat.LOGGER.error("Failed to list builtin pack resources", e);
        }
        return list.stream().filter(resourceLocation -> pathFilter.test(resourceLocation.getPath())).collect(Collectors.toList());
    }

    private static String convertPath(Path path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < path.getNameCount(); i++) {
            stringBuilder.append(path.getName(i));
            if (i < path.getNameCount() - 1) {
                stringBuilder.append("/");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public Set<String> getNamespaces(ResourcePackType packType) {
        if (packType == ResourcePackType.CLIENT_RESOURCES) {
            return ImmutableSet.of(Voicechat.MODID);
        }
        return ImmutableSet.of();
    }

    @Override
    public void close() {

    }
}
