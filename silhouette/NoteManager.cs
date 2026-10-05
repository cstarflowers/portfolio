using System.Collections;
using System.Collections.Generic;
using UnityEngine;
/* NOTE MANAGER (C#)
This is part two of a three-part series, which defines a note's position and velocity based on its encoded direction.
It utilizes the note's prefab naming scene (which contains their direction) to create a 3D vector and move each note to the corresponding location.
Then, it defines and utilizes several time variables to determine if the note has expired and needs to be freed to save memory.
*/
public class Note : MonoBehaviour
{
    double timeInstantiated;
    public float assignedTime;
    public string direction;

    void Start()
    {
        Application.targetFrameRate = 60;
        timeInstantiated = SongManager.GetAudioSourceTime();
    }

    void Update()
    {
        double timeSinceInstantiated = SongManager.GetAudioSourceTime() - timeInstantiated;
        float t = (float)(timeSinceInstantiated / (SongManager.Instance.noteTime * 2));

        if (t > 1)
        {
            Destroy(gameObject);
        }
        else
        {
        // Using directions, create a Vector3D corresponding to the note's down/up/left/right movement. The vectors are opposite the note's intended direction.
            if (this.gameObject.name.Contains("Up")):
                    transform.localPosition = Vector3.Lerp(Vector3.down * SongManager.Instance.noteSpawnY, Vector3.down * SongManager.Instance.noteDespawnY, t); 
                    break;
            if (this.gameObject.name.Contains("Down")):
                    transform.localPosition = Vector3.Lerp(Vector3.up * SongManager.Instance.noteSpawnY, Vector3.up * SongManager.Instance.noteDespawnY, t); 
                    break;
            if (this.gameObject.name.Contains("Right")):
                    transform.localPosition = Vector3.Lerp(Vector3.left * SongManager.Instance.noteSpawnX, Vector3.left * SongManager.Instance.noteDespawnX, t); 
                    break;
            if (this.gameObject.name.Contains("Left")):
                    transform.localPosition = Vector3.Lerp(Vector3.right * SongManager.Instance.noteSpawnX, Vector3.right * SongManager.Instance.noteDespawnX, t); 
                    break;
            }
            GetComponent<SpriteRenderer>().enabled = true;
        }
    }
}
