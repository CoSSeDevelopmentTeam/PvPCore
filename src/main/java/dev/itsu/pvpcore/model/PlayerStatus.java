package dev.itsu.pvpcore.model;

public class PlayerStatus {

    private String name; // 名前
    private int level; // プレイヤーのレベル
    private int experienceLevel; // プレイヤーの経験値
    private int matchCount; // 参加したマッチ数
    private int winCount; // 勝利数

    public PlayerStatus(String name, int level, int experienceLevel, int matchCount, int winCount) {
        this.name = name;
        this.level = level;
        this.experienceLevel = experienceLevel;
        this.matchCount = matchCount;
        this.winCount = winCount;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public int getWinCount() {
        return winCount;
    }
}
