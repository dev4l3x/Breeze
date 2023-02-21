package dev.asiglesias.infrastructure.notion.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotionObjectType {
    DATABASE("database"),
    PAGE("page");

    final String name;

}
