package com.learner.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.learner.model.questions.Question;

public class User {

    // User information
    private String email;
    private String username;
    private String displayName;
    private String password;
    private UUID uuid;
    private HashSet<ProgressTracker> progressTrackers; // Set of progress trackers for different languages

    // Addional user information settings
    private String profilePicturePath;
    private boolean readQuestionFeedbackAloud;


    /**
     * Constructor to initialize User with specified UUID
     */
    public User(String email, String username, String displayName, String password, UUID uuid) {
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.uuid = uuid;
        this.progressTrackers = new HashSet<>(); 
        profilePicturePath = "/com/learner/game/fxml-images/default-profile-picture.png";
        readQuestionFeedbackAloud = false;
    }

    /**
     * Constructor to initialize User without specified UUID (UUID will be generated)
     */
    public User(String email, String username, String displayName, String password) {
        this(email, username, displayName, password, generateUUID());
    }

    /**
     * Generates a new UUID
     * @return a randomly generated UUID
     */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }

    // Getters
    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public String changeEmail(String email) {
        if(UserList.getInstance().isEmailTaken(email)) {
            return "Email is taken";
        } else {
            this.email = email;
            replaceInList();
            return "Email changed successfully";
        }
    }

    public String changeUsername(String username) {
        if(UserList.getInstance().isUsernameTaken(username)) {
            return "Username is taken";
        } else {
            this.username = username;
            replaceInList();
            return "Username changed successfully";
        }
    }

    public String changeDisplayName(String displayName) {
        this.displayName = displayName;
        replaceInList();
        return "Display Name changed successfully";
    }

    public String changePassword(String password) {
        this.password = password;
        replaceInList();
        return "Password changed successfully";
    }

    private void replaceInList() {
        UserList.getInstance().replaceUser(this);
    }

    // Might implement this later
    public void setProfilePicture(String path, int size) throws IOException {
        // Ensure the file exists
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }
    
        // Detect file type (extension)
        String fileExtension = getFileExtension(file);
        if (fileExtension == null || !ImageIO.getImageReadersByFormatName(fileExtension).hasNext()) {
            throw new IOException("Unsupported file format: " + fileExtension);
        }
    
        // Read and resize the image
        BufferedImage originalImage = ImageIO.read(file);
        if (originalImage == null) {
            throw new IOException("Could not read image from file: " + path);
        }
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
    
        // Generate a new file name and save the resized image
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        File outputDir = new File("demo/src/main/resources/com/learner/game/user-profile-pictures/");
        outputDir.mkdirs(); // Ensure the directory exists
        File outputfile = new File(outputDir, newFileName);
        ImageIO.write(resizedImage, fileExtension, outputfile);
    
        // Set the profile picture path
        profilePicturePath = "/com/learner/game/user-profile-pictures/" + newFileName;
    }
    
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1).toLowerCase() : null;
    }

    /**
     * Gets the profile picture path
     * @return the path to the profile picture
     */
    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    /**
     * Sets the readQuestionFeedbackAloud setting
     * @param readQuestionFeedbackAloud
     * @return the new value of readQuestionFeedbackAloud
     */
    public boolean setReadQuestionFeedbackAloud(boolean readQuestionFeedbackAloud) {
        this.readQuestionFeedbackAloud = readQuestionFeedbackAloud;
        return readQuestionFeedbackAloud;
    }

    /**
     * Switches the readQuestionFeedbackAloud setting
     * @return the new value of readQuestionFeedbackAloud
     */
    public boolean setReadQuestionFeedbackAloud() {
        if(readQuestionFeedbackAloud) {
            return readQuestionFeedbackAloud = false;
        } 
        return readQuestionFeedbackAloud = true;
    }

    /**
     * Gets the readQuestionFeedbackAloud setting
     * @return the value of readQuestionFeedbackAloud
     */
    public boolean getReadQuestionFeedbackAloud() {
        return readQuestionFeedbackAloud;
    }

    // Progress Tracking

    public HashSet<ProgressTracker> getProgressTrackers() {
        return progressTrackers;
    }

    /**
     * Adds a progress tracker for a specified language
     */
    public void addProgressTracker(ProgressTracker tracker) {
        progressTrackers.add(tracker);
    }

    /**
     * Retrieves the ProgressTracker for a specific language by UUID
     * @param languageUUID the UUID of the language
     * @return the ProgressTracker for the specified language, or null if not found
     */
    public ProgressTracker getProgressTracker(UUID languageUUID) {
        for (ProgressTracker tracker : progressTrackers) {
            if (tracker.getUUID().equals(languageUUID)) {
                return tracker;
            }
        }
        return null;
    }

    /**
     * Adds a missed question to the progress tracker of the question's language
     */
    public void addMissedQuestion(Question question) {
        UUID questionLangUUID = question.getLanguageUUID();
        ProgressTracker currentProgressTracker = getProgressTracker(questionLangUUID);
        if (currentProgressTracker != null) {
            currentProgressTracker.addMissedQuestion(question);
        }
    }

    /**
     * Removes a missed question from the progress tracker of the question's language
     */
    public void removeMissedQuestion(Question question) {
        UUID questionLangUUID = question.getLanguageUUID();
        ProgressTracker currentProgressTracker = getProgressTracker(questionLangUUID);
        if (currentProgressTracker != null) {
            currentProgressTracker.removeMissedQuestion(question);
        }
    }

    // ptracker methods 
    public void addCompletedGame(UUID langUUID) {
        ProgressTracker currentProgressTracker = getProgressTracker(langUUID);
        if (currentProgressTracker != null) {
            currentProgressTracker.addCompletedGame(langUUID);
        } else {
            ProgressTracker newProgressTracker = new ProgressTracker(langUUID, GameManager.getInstance().getLanguageByUUID(langUUID).getLanguageName());
            newProgressTracker.addCompletedGame(langUUID);
            progressTrackers.add(newProgressTracker);
        }
    }

    public ArrayList<UUID> getCompletedGames(UUID langUUID) {
        ProgressTracker currentProgressTracker = getProgressTracker(langUUID);
        if (currentProgressTracker != null) {
            return currentProgressTracker.getCompletedGames();
        } else {
            return new ArrayList<>();
        }
    }

    public int getNumberOfCompletedGames(UUID langUUID) {
        ProgressTracker currentProgressTracker = getProgressTracker(langUUID);
        if (currentProgressTracker == null) {
            return 0;
        }
        return currentProgressTracker.getCompletedGames().size();
    }

    public int getTotalNumberOfCompletedGames() {
        int total = 0;
        for (Language lang : GameManager.getInstance().getAllLanguages()) {
            total += getNumberOfCompletedGames(lang.getUUID());
        }
        return total;
    }

    /**
     * Inner nested class used for tracking user progress.
     * Each ProgressTracker is tied to a language using its UUID.
     */
    public class ProgressTracker {
        
        private final ArrayList<Question> missedQuestions; // Stores missed questions directly
        private final ArrayList<UUID> completedGames;      // Stores UUIDs of completed games (if the user passes the quiz)
        private final UUID uuid;                           // Equal to languageUUID
        private final String languageName; 

        /**
         * Initializes ProgressTracker with the specified language UUID and name
         */
        public ProgressTracker(UUID languageUUID, String languageName) {
            this.uuid = languageUUID;
            this.languageName = languageName;
            this.missedQuestions = new ArrayList<>(); 
            this.completedGames = new ArrayList<>();  
        }

        // Getters
        public String getLanguageName() {
            return languageName;
        }

        public UUID getUUID() {
            return uuid;
        }
        
        public ArrayList<UUID> getCompletedGames() {
            return completedGames;
        }

        public ArrayList<Question> getMissedQuestions() {
            return missedQuestions;
        }
        
        /**
         * Adds a completed game by its UUID
         */
        public void addCompletedGame(UUID gameUUID) {
            if (!completedGames.contains(gameUUID)) {
                completedGames.add(gameUUID);
            }
        }
        
        /**
         * Adds a missed question to the tracker
         */
        public void addMissedQuestion(Question question) {
            if (!missedQuestions.contains(question)) {
                missedQuestions.add(question);
            }
        }
        
        /**
         * Removes a missed question from the tracker
         */
        public void removeMissedQuestion(Question question) {
            missedQuestions.remove(question);
        }

        /**
         * Retrieves a list of titles for completed games by looking up each UUID in GameManager
         * @return list of titles and difficulty levels of completed games
         */
        public ArrayList<String> getCompletedGamesTitles() {
            ArrayList<String> completedTitles = new ArrayList<>();
            GameManager gameManager = GameManager.getInstance();

            for (UUID gameUUID : completedGames) {
                Game game = gameManager.findGameByUUID(gameUUID);
                if (game != null) {
                    completedTitles.add(game.getDifficulty() + " " + game.getGameTitle());
                } 
            }

            return completedTitles;
        }

        public int getTotalCompletedGames() {
            return completedGames.size();
        }
    }
}
