package com.learner.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.learner.model.innerdata.GameCategory;
import com.learner.model.innerdata.TextObject;
import com.learner.model.questions.Question;

public class GameManager {

    private static GameManager instance; // Singleton instance

    // HashMap<key, value>, .key(---), .value(---)
    private final HashMap<UUID, Game> games;                      // Key = game uuid,       Value = instance of game 
    private final HashMap<UUID, ArrayList<UUID>> easyGameUUIDs;   // Key = language uuid,   Value = list of easy game uuids
    private final HashMap<UUID, ArrayList<UUID>> mediumGameUUIDs; // Key = language uuid,   Value = list of medium game uuids
    private final HashMap<UUID, ArrayList<UUID>> hardGameUUIDs;   // Key = language uuid,   Value = list of hard game uuids
    private final HashMap<Language, ArrayList<UUID>> languages;   // Key = Language,        Value = list of game uuids 
    private final HashMap<UUID, ArrayList<UUID>> textObjects;     // Key = game uuid,       Value = list of TextObject uuids 
    private final HashMap<UUID, ArrayList<UUID>> questions;       // Key = game uuid,       Value = list of Question UUIDs

    // Private instance to stasify singleton pattern
    private GameManager() {
        games = new HashMap<>();
        easyGameUUIDs = new HashMap<>();
        mediumGameUUIDs = new HashMap<>();
        hardGameUUIDs = new HashMap<>();
        languages = new HashMap<>();
        textObjects = new HashMap<>();
        questions = new HashMap<>();
    }

    /**
     * 
     * @return instance of GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * 
     * @param language
     */
    public void initializeLanguage(Language language) {
        languages.computeIfAbsent(language, k -> new ArrayList<>());
    }

    // Game Retrieval
    // Can be used to get game, then game can be used to get languageUUID, and other info
    public Game findGameByUUID(UUID gameUUID) {
        return games.get(gameUUID);
    }

    // Language Retrieval
    public Language getLanguageByUUID(UUID languageUUID) {
        for (Language language : languages.keySet()) {
            if (language.getUUID().equals(languageUUID)) {
                return language;
            }
        }
        return null;
    }

    public ArrayList<Language> getAllLanguages() {
        return new ArrayList<>(languages.keySet());
    }    

    public int getTotalNumberOfGames() {
        return games.size();
    }

    public int getNumberOfGamesForLanguage(UUID languageUUID) {
        int count = 0;
        for (Game game : games.values()) {
            if (game.getLanguageUUID().equals(languageUUID)) {
                count++;
            }
        }
        return count;
    }
    
    public int getNumberOfGamesForLanguage(String languageName) {
        Language language = findLanguage(languageName);
        if (language != null) {
            return getNumberOfGamesForLanguage(language.getUUID());
        }
        return 0; 
    }

    public int getNumberOfGamesForLanguage(Language language) {
        return getNumberOfGamesForLanguage(language.getUUID());
    }

    // public ArrayList<String> getLanguageNames() {
    //     ArrayList<String> languageNames = new ArrayList<>();

    //     for (Language language : languages.keySet()) {
    //         languageNames.add(language.getLanguageName());
    //     }

    //     return languageNames;
    // }

    /**
     * Add a game to all HashMaps where the game / game uuid needs to be accounted for
     */
    public void addGame(Game game) {
        UUID gameUUID = game.getUUID();
        games.put(gameUUID, game);

        HashMap<UUID, ArrayList<UUID>> difficultyMap;
        switch (game.getDifficulty()) {
            case EASY:
                difficultyMap = easyGameUUIDs;
                break;
            case MEDIUM:
                difficultyMap = mediumGameUUIDs;
                break;
            case HARD:
                difficultyMap = hardGameUUIDs;
                break;
            default:
                return;
            }

        difficultyMap.computeIfAbsent(game.getLanguageUUID(), k -> new ArrayList<>()).add(gameUUID);
        languages.computeIfAbsent(getLanguageByUUID(game.getLanguageUUID()), k -> new ArrayList<>()).add(gameUUID);
    }

    public Collection<Game> getAllGames() {
        return games.values();
    }

    /**
     * Method to grab needed games by category
     */
    public HashMap<GameCategory, ArrayList<Game>> getGamesByCategory(UUID languageUUID, Difficulty difficulty) {
        HashMap<GameCategory, ArrayList<Game>> categoryMap = new HashMap<>();
        
        // Select the appropriate difficulty map
        HashMap<UUID, ArrayList<UUID>> difficultyMap;
        switch (difficulty) {
            case EASY: difficultyMap = easyGameUUIDs; break;
            case MEDIUM: difficultyMap = mediumGameUUIDs; break;
            case HARD: difficultyMap = hardGameUUIDs; break;
            default: return categoryMap;
        }
        
        // Retrieve game UUIDs for the specified language
        ArrayList<UUID> gameUUIDs = difficultyMap.getOrDefault(languageUUID, new ArrayList<>());
        
        // Organize games by category
        for (UUID gameUUID : gameUUIDs) {
            Game game = games.get(gameUUID);
            if (game != null) {
                categoryMap.computeIfAbsent(game.getCategory(), k -> new ArrayList<>()).add(game);
            }
        }
        return categoryMap;
    }

    public Language findLanguage(String lang) {
        // Iterate through the keys (Language objects) in the languages map
        for (Language language : languages.keySet()) {
            // Compare the input string with the language name (case-insensitive)
            if (language.getLanguageName().equalsIgnoreCase(lang)) {
                return language; // Return the matching Language object
            }
        }
        return null; // Return null if no match is found
    }

    public Difficulty findDifficulty(String diff) {
        switch (diff) {
            case "easy":
                return Difficulty.EASY;
            case "medium":
                return Difficulty.MEDIUM;
            case "hard":
                return Difficulty.HARD;
        }
        return null;
    }


// TextObject Management

    /**
     * Add textObject to the textObjects HashMap
     */
    public void addTextObjectUUID(UUID gameUUID, UUID textObjectUUID) {
        textObjects.computeIfAbsent(gameUUID, k -> new ArrayList<>()).add(textObjectUUID);
    }

    /**
     * Search for textObject based from gameUUID & textObject
     */
    public TextObject findTextObjectInGame(UUID gameUUID, UUID textObjectUUID) {
        Game game = findGameByUUID(gameUUID);
        return (game != null) ? game.getTextObject(textObjectUUID) : null;
    }

    /**
     * Search for a TextObject based on its UUID across all games.
     * 
     * @return TextObject
     */
    public TextObject findTextObjectByUUID(UUID textObjectUUID) {
        for (Game game : getAllGames()) { 
            TextObject textObject = game.getTextObject(textObjectUUID);
            if (textObject != null) {
                return textObject; // Return the found TextObject
            }
        }
        return null; // Return null if not found in any game
    }

    /**
     * Get the list of TextObject UUIDs for a given gameUUID.
     *
     * @param gameUUID the UUID of the game
     * @return an ArrayList of TextObject UUIDs associated with the game, or an empty list if none are found
     */
    public ArrayList<UUID> getTextObjectUUIDs(UUID gameUUID) {
        return textObjects.getOrDefault(gameUUID, new ArrayList<>());
    }

// Question Management

    /**
     * 
     */
    public void addQuestionUUID(UUID gameUUID, UUID questionUUID) {
        questions.computeIfAbsent(gameUUID, k -> new ArrayList<>()).add(questionUUID);
    }

    /**
     * 
     * @param gameUUID
     * @param questionUUID
     * @return
     */
    public Question findQuestionInGame(UUID gameUUID, UUID questionUUID) {
        Game game = findGameByUUID(gameUUID);
        return (game != null) ? game.getQuestion(questionUUID) : null;
    }

    /**
     * 
     */
    public ArrayList<UUID> getQuestionUUIDs(UUID gameUUID) {
        return questions.getOrDefault(gameUUID, new ArrayList<>());
    }

    /**
     * Get questions of a game, by using the game uuid
     * 
     * @param gameUUID
     * @return
     */
    public ArrayList<Question> getQuestions(UUID gameUUID) {
        Game game = findGameByUUID(gameUUID);
        return (game != null) ? game.getQuestions() : new ArrayList<>();
    }

// Other methods

    /**
     * 
     * @return gameUUID that the targetUUID (question or textObject, is contained within)
     */
    public UUID findGameUUIDByQuestionOrTextObjectUUID(UUID targetUUID) {
        // First, search in the questions HashMap
        for (HashMap.Entry<UUID, ArrayList<UUID>> entry : questions.entrySet()) {
            UUID gameUUID = entry.getKey();
            ArrayList<UUID> questionList = entry.getValue();
    
            // Check if the targetUUID is in the list of question UUIDs
            if (questionList.contains(targetUUID)) {
                return gameUUID;  // Return gameUUID if found in questions
            }
        }
    
        // If not found in questions, search in the textObjects HashMap
        for (HashMap.Entry<UUID, ArrayList<UUID>> entry : textObjects.entrySet()) {
            UUID gameUUID = entry.getKey();
            ArrayList<UUID> textObjectList = entry.getValue();
    
            // Check if the targetUUID is in the list of textObject UUIDs
            if (textObjectList.contains(targetUUID)) {
                return gameUUID;  // Return gameUUID if found in textObjects
            }
        }
    
        // Return null if not found in either HashMap
        return null;
    }

    public ArrayList<Game> getGamesByDifficulty(UUID languageUUID, Difficulty difficulty) {
        ArrayList<Game> gamesByDifficulty = new ArrayList<>();
        ArrayList<UUID> gameUUIDs;

        switch (difficulty) {
            case EASY:
                gameUUIDs = easyGameUUIDs.getOrDefault(languageUUID, new ArrayList<>());
                break;
            case MEDIUM:
                gameUUIDs = mediumGameUUIDs.getOrDefault(languageUUID, new ArrayList<>());
                break;
            case HARD:
                gameUUIDs = hardGameUUIDs.getOrDefault(languageUUID, new ArrayList<>());
                break;
            default:
                gameUUIDs = new ArrayList<>();
                break;
        }  
    
        for (UUID gameUUID : gameUUIDs) {
            Game game = games.get(gameUUID);
            if (game != null) {
                gamesByDifficulty.add(game);
            }
        }
        return gamesByDifficulty;
    }
    

    // toString Method for Debugging
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\u001B[33m").append("GAME MANAGER TO STRING:").append("\u001B[0m\n");
    
        // Iterate through each language
        for (Language language : languages.keySet()) {
            s.append(language.toString()).append("\n");
            s.append("Easy ").append(language.getLanguageName()).append(" games:\n");
            s.append(easyGameUUIDs.get(language.getUUID())).append("\n");
            s.append("Medium ").append(language.getLanguageName()).append(" games:\n");
            s.append(mediumGameUUIDs.get(language.getUUID())).append("\n");
            s.append("Hard ").append(language.getLanguageName()).append(" games:\n");
            s.append(hardGameUUIDs.get(language.getUUID())).append("\n");
        }
    
        s.append("\u001B[33m").append("END OF GAME MANAGER TO STRING\n\n").append("\u001B[0m");
        return s.toString();
    }
}
