package cn.newcraft.terminal.update;

public interface Update {

    void checkUpdate(boolean ret);

    void confirmUpdate();

    void startUpdate();

    boolean isUpdate();

    /*private String canonicalVersion;
    private String version;
    private String newVersion;
    private String description;
    private static boolean update = false;
    private Screen screen = Terminal.getScreen();

    public static boolean isUpdate() {
        return update;
    }

    public static void setUpdate(boolean b){
        update = b;
    }

    public Update(String canonicalVersion) {
        try {
            this.canonicalVersion = canonicalVersion;
            URL url = new URL("https://api.newcraft.cn/update.php?version=" + canonicalVersion);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String response = reader.readLine();
            this.newVersion = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("canonical");
            this.version = JSONObject.parseObject(response).getJSONObject("Terminal").getJSONObject("latest").getString("version");
            this.description = JSONObject.parseObject(response).getJSONObject("Terminal").getString("description");
        } catch (IOException e) {
            Method.printException(this.getClass(), e);
        }
    }

    public void check(boolean ret) {
        if (newVersion != null && !newVersion.equals(canonicalVersion)) {
            if (screen.getGraphicalScreen() != null) {
                PromptScreen promptScreen = new PromptScreen();
                JButton determine = new JButton("点击更新");
                determine.setFont(new Font("宋体", Font.PLAIN, 14));
                determine.setBounds(120, 140, 70, 30);
                determine.setCursor(new Cursor(12));
                determine.setContentAreaFilled(false);
                determine.setBorder(BorderFactory.createRaisedBevelBorder());
                determine.setBackground(Color.decode("#3366FF"));
                determine.addActionListener(arg0 -> {
                    update();
                    promptScreen.close();
                });
                promptScreen.show("检测到有新版本！",
                        "当前版本：" + Terminal.getOptions().getVersion() + " (" + canonicalVersion + ")\n" +
                                "最新版本：" + version + " (" + newVersion + ")\n\n" +
                                "更新日志：" + description, 8000, determine);
            } else {
                screen.sendMessage("-----检测到有新版本-----");
                screen.sendMessage("当前版本：" + Terminal.getOptions().getVersion() + " (" + canonicalVersion + ")");
                screen.sendMessage("最新版本：" + version + " (" + newVersion + ")\n");
                screen.sendMessage("更新日志：" + description);
                screen.sendMessage("更新请输入命令 \"update latest\" 即可进行更新操作！");
            }
        } else if (ret) {
            screen.sendMessage("当前版本已为最新版本！");
        }
    }

    public void update(){
        screen.onUpdate(newVersion);
    }

     */
}
