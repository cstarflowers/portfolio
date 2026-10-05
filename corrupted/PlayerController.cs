using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

/* Player Controller (C#)
This class is used to control the player's movement, including when they're colliding with enemies and must attack. 
For the most part, this system is fairly straight forward, using Vector2D's to move in the direction the player commands.
When necessary, this code will also queue movement in the animator and render the enemy/player's HP!
*/

public class PlayerController : MonoBehaviour {

public string nextLevel;
public string enemyLevel;
static private int enemyDefeats = 0;
public int health;
static private int maxHealth;
public TextMeshProUGUI healthText;
public TextMeshProUGUI enemyDamageText;
public int enemyHealth = 999;
private int enemyDamage = 0;

public float speed;
private float devSpeed;
public Rigidbody2D player;
public Animator animator;
private bool canHit = false;
private Collider2D enemyObj;
public GameObject enemyAnimator;

public float verticalRaw;
private float verticalAbs;
public float horizontalRaw;
private float horizontalAbs;

private int baseEnemyHealth;

void Start() {
    health = health + (5 * enemyDefeats);

    devSpeed = speed;
    Application.targetFrameRate = 60;
    GetComponent<Rigidbody2D>().constraints = RigidbodyConstraints2D.FreezeRotation;

    baseEnemyHealth = enemyHealth;
    if(enemyDamageText != null) {
        enemyDamageText.text = "Enemy HP:" + enemyHealth + "/" + baseEnemyHealth;
    }
    if(healthText != null) {
        healthText.text = "HP:" + health + "/" + maxHealth;
    }
    // ORIGINAL CODE BY CG SMOOTHIE (https://www.youtube.com/watch?v=1YKfBh1FCWY)
    // WITH CODE FROM NIGHT RUN STUDIO (https://www.youtube.com/watch?v=d9bcmvr_Me4) 
    // NTS: DO NOT ADD AS CHILD OF PLAYER
}

void Update() {
    maxHealth = 100 + (5 * enemyDefeats);
    if(Input.GetKey(KeyCode.LeftShift) || Input.GetKey(KeyCode.RightShift)) {
        animator.speed = 1.5f;
        speed = devSpeed * 2f;
    }
    else {
        animator.speed = 1f;
        speed = devSpeed;
    }
    verticalRaw = Input.GetAxisRaw("Vertical");
    horizontalRaw = Input.GetAxisRaw("Horizontal");
    verticalAbs = Mathf.Abs(verticalRaw);
    horizontalAbs = Mathf.Abs(horizontalRaw);
}

void FixedUpdate() {
    if (verticalAbs > 0 || horizontalAbs > 0) {
        animator.SetBool("isWalking", true);

        if (horizontalAbs == 1) {
            animator.SetFloat("MoveX", horizontalRaw);
            animator.SetFloat("MoveY", 0);
            player.AddForce(new Vector2(horizontalRaw * speed, 0));
        }

        else if (verticalAbs == 1) {
            animator.SetFloat("MoveY", verticalRaw);
            animator.SetFloat("MoveX", 0);
            player.AddForce(new Vector2(0, verticalRaw * speed));
            }
        }

    else {
        animator.SetBool("isWalking", false);
    }

    if(canHit && (Input.GetKey(KeyCode.Return) || Input.GetKey(KeyCode.Space))) {
        canHit = false;
        Destroy(enemyObj.gameObject);
        enemyDamage = health / 2;
        enemyHealth -= enemyDamage;
        if(enemyAnimator != null) {
            enemyAnimator.SetActive(true);
            enemyAnimator.GetComponent<SpriteRenderer>().enabled = true;
        }
        // Debug.Log("[Debug] Enemy HP: " + enemyHealth + "/" + baseEnemyHealth + " (" + enemyDamage + " damage)");
        
        if(enemyDamageText != null && enemyHealth >= 0) {
            enemyDamageText.text = "Enemy HP:" + enemyHealth + "/" + baseEnemyHealth;
        }
        if(enemyHealth <= 0) {
            enemyDefeats += 1;
            this.enabled = false;
            Initiate.Fade(nextLevel,Color.black,5);
        }
    }
}

void OnTriggerEnter2D(Collider2D enemy) {
    if(enemy.gameObject.tag == "Enemy") {
        Destroy(enemy.gameObject);
        health -= 20;
        if(healthText != null) {
            healthText.text = "HP:" + health + "/" + maxHealth;
        }
        if(health <= 0) {
            this.enabled = false;
            Initiate.Fade(enemyLevel,Color.white,5);
        }
    }
    else if(enemy.gameObject.tag == "AttackPoint") {
        canHit = true;
        enemyObj = enemy;
    }
}

void OnTriggerExit2D(Collider2D enemy) {
    if(enemy.gameObject.tag == "AttackPoint") {
        canHit = false;
        StopCoroutine(StopHitAnimation());
        StartCoroutine(StopHitAnimation());
    }
}


IEnumerator StopHitAnimation() {
    yield return new WaitForSeconds(0.6f);
        if(enemyAnimator != null) {
            enemyAnimator.SetActive(false);
            enemyAnimator.GetComponent<SpriteRenderer>().enabled = false;
        }
    }
}
