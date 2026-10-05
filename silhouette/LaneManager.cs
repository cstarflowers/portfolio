using Melanchall.DryWetMidi.Interaction;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
/* LANE MANAGER (C#)
This code is part of a 3-part series responsible for rendering notes from a MIDI file. 
In particular, this piece spawns the notes based on their assigned timestamps, converting the timestamp of the MIDI file into milliseconds.
Then, when the user clicks, the program calculates the marginOfError by finding the millisecond of the click and subtracting it from the millisecond the note spawned.
If the difference is greater than it would take the note to reach the edge of the circle, it's considered a miss; otherwise, a hit.
*/
public class Lane : MonoBehaviour
{
    public Melanchall.DryWetMidi.MusicTheory.NoteName noteRestriction;
    public KeyCode input;
    public GameObject notePrefab;
    List<Note> notes = new List<Note>();
    public List<double> timeStamps = new List<double>();
    public AudioSource hitSound1;
    public AudioSource hitSound2;
    public AudioSource hitSound3;
    public GameObject hitEffect;

    int spawnIndex = 0;
    int inputIndex = 0;

    public void SetTimeStamps(Melanchall.DryWetMidi.Interaction.Note[] array)
    {
        foreach (var note in array)
        {
            if (note.NoteName == noteRestriction)
            {
            // Converts each note's timestamp into milliseconds via the MIDI file
                var metricTimeSpan = TimeConverter.ConvertTo<MetricTimeSpan>(note.Time, SongManager.midiFile.GetTempoMap());
                timeStamps.Add((double)metricTimeSpan.Minutes * 60f + metricTimeSpan.Seconds + (double)metricTimeSpan.Milliseconds / 1000f);
            }
        }
    }
    void Update()
    {
        if (spawnIndex < timeStamps.Count)
        {
            if (SongManager.GetAudioSourceTime() >= timeStamps[spawnIndex] - SongManager.Instance.noteTime)
            {
            // Spawn the note encoded on the MIDI track
                var note = Instantiate(notePrefab, transform);
                notes.Add(note.GetComponent<Note>());
                note.GetComponent<Note>().assignedTime = (float)timeStamps[spawnIndex];
                spawnIndex++;
            }
        }

        if (inputIndex < timeStamps.Count)
        {
            double timeStamp = timeStamps[inputIndex];
            double marginOfError = SongManager.Instance.marginOfError;
            double audioTime = SongManager.GetAudioSourceTime() - (SongManager.Instance.inputDelayInMilliseconds / 1000.0);

          // When the user clicks, calculate the margin of error and determine whether or not the note was hit on time
          // If it was, count is as a hit and destroy the note
          // Otherwise, count it as a miss
            if (Input.GetKeyDown(input))
            {
                if (Math.Abs(audioTime - timeStamp) < marginOfError)
                {
                    Hit();
                    Instantiate(hitEffect, notes[inputIndex].transform.position, notes[inputIndex].transform.rotation);
                    // print($"Hit on {inputIndex} note");
                    Destroy(notes[inputIndex].gameObject);
                    inputIndex++;
                }
                else
                {
                    Miss();
                    // print($"Hit inaccurate on {inputIndex} note with {Math.Abs(audioTime - timeStamp)} delay");
                }
            }
            if (timeStamp + marginOfError <= audioTime)
            {
                Miss();
                // print($"Missed {inputIndex} note");
                inputIndex++;
            }
        }       
    
    }

    private void Hit()
    {
    // When a note is hit, inform the ScoreManager to increase the combo and points
    // Then, play a random hit sound, 1-3.
        ScoreManager.Hit();
        var hitSoundNum = UnityEngine.Random.Range(1,3);
        switch(hitSoundNum) {
            case 1:
                hitSound1.Play();
                break;
            case 2:
                hitSound2.Play();
                break;
            case 3:
                hitSound3.Play();
                break;
        }
    }

    private void Miss()
    {
        ScoreManager.Miss();
    }
}
