using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
/* TYPING EFFECTS (C#)
This piece of code is extracted from the text renderer I created for Silhouette. 
This code is specifically ran anytime the user engages with an enemy for the first time, 
which will begin a brief line of dialogue before they actually engage in battle. 
Typically, this dialogue has multiple versions or lines and can change each time its run, depending on when a reset is called. 
*/
public class TypingEffect : MonoBehaviour {
    private float delay = 0.04f;
    private string currentText = " ";

    public string[] textArray;

    public TextMeshProUGUI textObject;
    public AudioSource textSound;
    public GameObject dialogueBox;

    private bool isColliding;
    private bool inUse = false;
    private int onText = 0;
    public Rigidbody2D player;

    private PlayerController playerController;

  // Ensure the player is not null on startup, set as nearest "PlayerController" object
    void Start() {
        playerController = player.GetComponent<PlayerController>();

    }

    void Update() {
      // If ENTER or Left Mouse are pressed, check if text is already being displayed and is eligible for display (isColliding). 
      // If text cycle is complete, reset it from -1 to 0. Then, check for commands (STOP, GOTO NEXT)
        if(Input.GetKeyDown(KeyCode.Return) || Input.GetMouseButtonDown(0)) {
            if(isColliding && !inUse) {
                if(onText == -1) {
                    onText = 0;
                }
              // If the next piece of text is "STOP", hide the text box and reset the cycle to -1
              // Enable the playerController to allow movement
                else if(textArray[onText].ToString() == "STOP") {
                    textSound.Stop();
                    dialogueBox.SetActive(false);
                    playerController.enabled = true;
                    onText = -1;
                }
              // If the next piece of text is "GOTO NEXT", hide the text box and change scenes using a fade out
              // Reset the playerController to allow for movement
                else if(textArray[onText].ToString() == "GOTO NEXT") {
                    textSound.Stop();
                    dialogueBox.SetActive(false);
                    playerController.enabled = true;
                    Initiate.Fade(textArray[onText+1].ToString(),Color.black,15);
                    onText = 0;

                }
              // If the next line is not a command, then it must be another line of dialogue
              // Start a coroutine to render the dialogue and increase the dialogue counter by 1
                else if(textArray[onText].ToString() != "STOP" && textArray[onText].ToString() != "GOTO NEXT") {
                    StartCoroutine(showText(textArray[onText]));
                    onText += 1;
                }
            }
        }
    }

    public IEnumerator showText(string displayText) {
      // Reset the text being displayed and ensure the textbox and movement are correctly set-up
        inUse = true;
        disableMovement();
        currentText = " ";
        dialogueBox.SetActive(true);
        textSound.Play();
      // For the entire length of the display text, slowly render the text using a WaitForSeconds(delay), rather than showing it all at once
      // When all of the text has been rendered, allow the user to click out of the text box and stop the display sound
        for(int i = 0; i <= displayText.Length; i++) {
            currentText = displayText.Substring(0,i);
            textObject.text = currentText;
            yield return new WaitForSeconds(delay);
        }
        textSound.Stop();
        inUse = false;
    }

  // Define simple utility functions for when the player can spawn a text box (isColliding)
    void OnTriggerEnter2D(Collider2D other) {
        if(other.gameObject.name == "Player") {
            isColliding = true;
        }
    }

    void OnTriggerExit2D(Collider2D other) {
        if(other.gameObject.name == "Player") {
            isColliding = false;
            dialogueBox.SetActive(false);
            playerController.enabled = true;
            onText = 0;
        }
    }

  // Define simple utility function to disable player movement by resetting their 2D movement vectors and disabling the movement animator
    void disableMovement() {
        playerController.enabled = false;
        
        PlayerController.direction = new Vector2(0,0);
        player.velocity = new Vector2(0,0);

        PlayerController.animator.SetBool("walkingLeft", false);
        PlayerController.animator.SetBool("walkingRight", false);
        PlayerController.animator.SetBool("walkingForward", false);
        PlayerController.animator.SetBool("walkingBackward", false);
    }
}
