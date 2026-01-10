export const weeks = [
  {
      id: 1,
      title: "Week 1 - Arrays & Strings",
      problems: [
        { id: 'A', title: "Sum of Two Numbers", difficulty: "Easy", solved: 1234, total: 1500, accuracy: 82.3 },
        { id: 'B', title: "Array Rotation", difficulty: "Easy", solved: 987, total: 1450, accuracy: 68.1 },
        { id: 'C', title: "String Palindrome", difficulty: "Medium", solved: 654, total: 1200, accuracy: 54.5 },
        { id: 'D', title: "Maximum Subarray", difficulty: "Hard", solved: 321, total: 890, accuracy: 36.1 }
      ]
    },
    {
      id: 2,
      title: "Week 2 - Sorting & Searching",
      problems: [
        { id: 'A', title: "Binary Search", difficulty: "Easy", solved: 1100, total: 1350, accuracy: 81.5 },
        { id: 'B', title: "Merge Sort", difficulty: "Medium", solved: 780, total: 1100, accuracy: 70.9 },
        { id: 'C', title: "Quick Select", difficulty: "Medium", solved: 560, total: 980, accuracy: 57.1 },
        { id: 'D', title: "Search in Rotated Array", difficulty: "Hard", solved: 290, total: 750, accuracy: 38.7 }
      ]
    },
    {
      id: 3,
      title: "Week 3 - Dynamic Programming",
      problems: [
        { id: 'A', title: "Fibonacci Numbers", difficulty: "Easy", solved: 950, total: 1200, accuracy: 79.2 },
        { id: 'B', title: "Coin Change", difficulty: "Medium", solved: 670, total: 1050, accuracy: 63.8 },
        { id: 'C', title: "Longest Increasing Subsequence", difficulty: "Hard", solved: 450, total: 890, accuracy: 50.6 },
        { id: 'D', title: "Edit Distance", difficulty: "Hard", solved: 280, total: 720, accuracy: 38.9 }
      ]
    }
];

export const successfulSubmissions = [
  { id: 1, username: "code_master_99", language: "C++", time: "15ms", memory: "2.1MB", timestamp: "2 hours ago", canHack: true },
    { id: 2, username: "algo_wizard", language: "Python", time: "45ms", memory: "3.5MB", timestamp: "3 hours ago", canHack: true },
    { id: 3, username: "binary_beast", language: "Java", time: "28ms", memory: "4.2MB", timestamp: "5 hours ago", canHack: true },
    { id: 4, username: "dp_dynamo", language: "C++", time: "12ms", memory: "1.9MB", timestamp: "6 hours ago", canHack: true },
    { id: 5, username: "graph_guru", language: "Python", time: "52ms", memory: "3.8MB", timestamp: "8 hours ago", canHack: true },
    { id: 6, username: "tree_traverser", language: "C++", time: "18ms", memory: "2.3MB", timestamp: "10 hours ago", canHack: true },
    { id: 7, username: "heap_hero", language: "Java", time: "31ms", memory: "4.5MB", timestamp: "12 hours ago", canHack: true },
    { id: 8, username: "sort_sensei", language: "Python", time: "48ms", memory: "3.6MB", timestamp: "14 hours ago", canHack: true }
];

export const leaderboard = [
  { rank: 1, username: "code_master_99", score: 2850, solved: 45, country: "USA" },
    { rank: 2, username: "algo_wizard", score: 2720, solved: 42, country: "China" },
    { rank: 3, username: "binary_beast", score: 2680, solved: 41, country: "India" },
    { rank: 4, username: "dp_dynamo", score: 2590, solved: 39, country: "Russia" },
    { rank: 5, username: "graph_guru", score: 2540, solved: 38, country: "Japan" },
    { rank: 6, username: "tree_traverser", score: 2480, solved: 36, country: "Germany" },
    { rank: 7, username: "heap_hero", score: 2420, solved: 35, country: "Brazil" },
    { rank: 8, username: "sort_sensei", score: 2380, solved: 34, country: "Canada" },
    { rank: 9, username: "stack_samurai", score: 2340, solved: 33, country: "UK" },
    { rank: 10, username: "queue_queen", score: 2290, solved: 32, country: "France" }
];

export const problemData = {
  title: "A. Sum of Two Numbers",
    timeLimit: "1 second",
    memoryLimit: "256 megabytes",
    statement: `You are given an integer n. Find two integers a and b such that a + b = n.`,
    inputFormat: "The first line contains a single integer t (1 ≤ t ≤ 1000) — the number of test cases.\n\nEach test case consists of one line containing a single integer n (1 ≤ n ≤ 10^9).",
    outputFormat: "For each test case, print two integers a and b such that a + b = n. If there are multiple solutions, print any of them.",
    examples: [
      {
        input: "3\n5\n100\n1",
        output: "2 3\n50 50\n0 1"
      }
    ],
    note: "In the first test case, 2 + 3 = 5.\nIn the second test case, 50 + 50 = 100.\nIn the third test case, 0 + 1 = 1."
};

export const sampleSubmissionCode = `#include <iostream>
using namespace std;

int main() {
    int t;
    cin >> t;
    while(t--) {
        long long n;
        cin >> n;
        cout << n/2 << " " << n - n/2 << endl;
    }
    return 0;
}`;