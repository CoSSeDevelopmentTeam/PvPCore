package dev.itsu.pvpcore.model;

// 闘技場のデータクラス
public class Arena {

    private int id; // 闘技場ID
    private String name; // 名前
    private String owner; // 作成者
    private String description; // 説明
    private String world; // ワールド
    private int x; // 座標
    private int y;
    private int z;
    private Status status; // ステータス

    public Arena(int id, String name, String owner, String description, String world, int x, int y, int z, Status status) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Status getStatus() {
        return status;
    }

    // 闘技場の状態を示すステータス
    public enum Status {
        AVAILABLE(0), // 使用可能
        RESERVED(1), // 予約済み（作成されたルームで使われている）
        USED(2), // 使用中
        UNKNOWN(3); // 不明

        private int s;

        Status(int s) {
            this.s = s;
        }

        public static Status fromInt(int s) {
            switch (s) {
                case 0: return AVAILABLE;
                case 1: return RESERVED;
                case 2: return USED;
                default: return UNKNOWN;
            }
        }

        public int toInt() {
            return s;
        }
    }

}
