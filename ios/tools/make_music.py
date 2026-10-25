#!/usr/bin/env python3
"""Synthesises the background music: a spooky chiptune loop in A minor.

Usage: python3 make_music.py   (needs numpy)
Writes ZombieWorld/Audio/theme.wav (mono, 22.05 kHz), which loops seamlessly.
"""
import os
import wave

import numpy as np

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "..", "ZombieWorld", "Audio", "theme.wav")

RATE = 22050
BPM = 92
EIGHTH = 60 / BPM / 2
BAR = EIGHTH * 8

NOTE_INDEX = {"C": 0, "C#": 1, "D": 2, "D#": 3, "E": 4, "F": 5, "F#": 6, "G": 7, "G#": 8, "A": 9, "A#": 10, "B": 11}


def freq(name):
    note, octave = name[:-1], int(name[-1])
    midi = 12 * (octave + 1) + NOTE_INDEX[note]
    return 440.0 * 2 ** ((midi - 69) / 12)


CHORDS = {
    "Am": ["A", "C", "E"], "F": ["F", "A", "C"], "Dm": ["D", "F", "A"],
    "E": ["E", "G#", "B"], "G": ["G", "B", "D"],
}
PROGRESSION = ["Am", "F", "Dm", "E", "Am", "F", "Dm", "E",
               "Am", "G", "F", "E", "Dm", "Am", "E", "E"]

# (note or None for a rest, length in eighths); each bar adds up to 8
MELODY = [
    [("E5", 2), ("C5", 1), ("A4", 1), ("E5", 2), ("D5", 1), ("C5", 1)],
    [("C5", 2), ("A4", 1), ("F4", 1), ("A4", 2), ("C5", 2)],
    [("D5", 3), ("F5", 1), ("E5", 2), ("D5", 2)],
    [("B4", 2), ("G#4", 2), ("E4", 4)],
    [("A4", 1), ("C5", 1), ("E5", 1), ("A5", 1), ("G#5", 2), ("E5", 2)],
    [("F5", 3), ("E5", 1), ("D5", 2), ("C5", 2)],
    [("D5", 2), ("F5", 2), ("E5", 2), ("D5", 1), ("C5", 1)],
    [("B4", 4), ("G#4", 2), (None, 2)],
    [("A4", 4), (None, 2), ("E4", 2)],
    [("G4", 3), ("B4", 1), ("D5", 4)],
    [("C5", 3), ("A4", 1), ("F4", 4)],
    [("G#4", 2), ("B4", 2), ("D5", 2), ("F5", 2)],
    [("F5", 2), ("E5", 2), ("D5", 2), ("A4", 2)],
    [("C5", 2), ("B4", 2), ("A4", 4)],
    [("G#4", 2), ("A4", 2), ("B4", 2), ("D5", 2)],
    [("E5", 6), (None, 2)],
]

LENGTH = int(round(BAR * len(PROGRESSION) * RATE))


def envelope(n, attack=0.01, decay=0.08, sustain=0.6, release=0.06):
    t = np.arange(n) / RATE
    env = np.full(n, sustain)
    a, d, r = int(attack * RATE), int(decay * RATE), int(release * RATE)
    if a:
        env[:a] = np.linspace(0, 1, a)[: min(a, n)] if a <= n else np.linspace(0, 1, n)
    if d and a < n:
        end = min(a + d, n)
        env[a:end] = np.linspace(1, sustain, d)[: end - a]
    if r and n > r:
        env[-r:] *= np.linspace(1, 0, r)
    return env


def pulse(f, n, duty, vibrato=0.0):
    t = np.arange(n) / RATE
    phase = f * t
    if vibrato:
        # vibrato fades in after a moment, like a held chip note
        depth = vibrato * np.clip((t - 0.15) / 0.3, 0, 1)
        phase = np.cumsum(f * (1 + depth * np.sin(2 * np.pi * 5.5 * t))) / RATE
    return np.where((phase % 1.0) < duty, 1.0, -1.0)


def triangle(f, n):
    t = np.arange(n) / RATE
    return 2 * np.abs(2 * ((f * t) % 1.0) - 1) - 1


def add(track, start, samples):
    start = int(round(start * RATE))
    end = min(start + len(samples), LENGTH)
    track[start:end] += samples[: end - start]
    # Wrap anything that spills past the end, so the loop is seamless.
    spill = len(samples) - (end - start)
    if spill > 0:
        track[:spill] += samples[end - start:]


def render():
    lead = np.zeros(LENGTH)
    arp = np.zeros(LENGTH)
    bass = np.zeros(LENGTH)
    drums = np.zeros(LENGTH)
    rng = np.random.default_rng(13)

    for bar, chord in enumerate(PROGRESSION):
        bar_start = bar * BAR
        tones = CHORDS[chord]

        # Lead melody: 25% pulse with vibrato on long notes
        t = bar_start
        for note, length in MELODY[bar]:
            duration = length * EIGHTH
            if note:
                n = int(duration * RATE)
                wave_ = pulse(freq(note), n, 0.25, vibrato=0.012 if length >= 3 else 0.0)
                add(lead, t, wave_ * envelope(n, decay=0.12, sustain=0.55, release=0.05))
            t += duration

        # Arpeggio: 16th notes up and down the chord, 12.5% pulse
        pattern = [tones[0] + "4", tones[1] + "4", tones[2] + "4", tones[0] + "5"]
        pattern += pattern[-2:0:-1]
        sixteenth = EIGHTH / 2
        for i in range(16):
            name = pattern[i % len(pattern)]
            if name.startswith(("C", "D")) and name.endswith("4") and tones[0] in ("A", "G", "E"):
                name = name[:-1] + "5"  # keep notes above the root
            n = int(sixteenth * RATE)
            add(arp, bar_start + i * sixteenth, pulse(freq(name), n, 0.125) * envelope(n, 0.003, 0.05, 0.3, 0.02))

        # Bass: triangle, root and octave in eighths
        root = tones[0]
        for i, octave in enumerate([2, 2, 3, 2, 2, 2, 3, 2]):
            n = int(EIGHTH * RATE)
            add(bass, bar_start + i * EIGHTH, triangle(freq(root + str(octave)), n) * envelope(n, 0.005, 0.1, 0.7, 0.03))

        # Drums: soft kick on 1 and 3, closed hats on the off-beats, a rim on 4 in the second half
        for beat in (0, 4):
            n = int(0.18 * RATE)
            t_k = np.arange(n) / RATE
            kick = np.sin(2 * np.pi * np.cumsum(55 + 90 * np.exp(-t_k * 30)) / RATE) * np.exp(-t_k * 18)
            add(drums, bar_start + beat * EIGHTH, kick * 0.9)
        for i in range(1, 8, 2):
            n = int(0.04 * RATE)
            hat = rng.uniform(-1, 1, n) * np.exp(-np.arange(n) / RATE * 90)
            hat = np.diff(hat, prepend=0)  # crude high-pass
            add(drums, bar_start + i * EIGHTH, hat * 0.35)
        if bar >= 8:
            n = int(0.08 * RATE)
            rim = rng.uniform(-1, 1, n) * np.exp(-np.arange(n) / RATE * 45)
            add(drums, bar_start + 6 * EIGHTH, rim * 0.3)

    mix = 0.30 * lead + 0.10 * arp + 0.32 * bass + 0.28 * drums
    mix -= mix.mean()  # pulse waves aren't centred on zero

    # Soften the square waves a little (one-pole low-pass), then a wrapped echo for space.
    alpha = 0.35
    smooth = np.empty_like(mix)
    acc = 0.0
    for _ in range(2):  # second pass settles the filter state across the loop point
        for i, x in enumerate(mix):
            acc += alpha * (x - acc)
            smooth[i] = acc
    delay = int(EIGHTH * 3 * RATE)
    echoed = smooth + 0.28 * np.roll(smooth, delay) + 0.12 * np.roll(smooth, 2 * delay)

    echoed /= np.max(np.abs(echoed)) / 0.85
    return echoed


def main():
    audio = render()
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with wave.open(OUT, "wb") as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(RATE)
        f.writeframes((audio * 32767).astype("<i2").tobytes())
    print("Wrote %s (%.1f s)" % (os.path.normpath(OUT), len(audio) / RATE))


if __name__ == "__main__":
    main()
