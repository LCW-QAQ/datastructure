#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAXIMUM_LETTER 26

typedef struct Node {
  int pass;
  int end;
  struct Node* nodes[MAXIMUM_LETTER];
} Node;

Node* newNode();
void freeNode(Node* root);

Node* newNode() {
  Node* node = (Node*)malloc(sizeof(Node));
  for (int i = 0; i < MAXIMUM_LETTER; i++) {
    node->pass = 0;
    node->end = 0;
    node->nodes[i] = NULL;
  }
  return node;
}

void insertNode(Node* node, char* str) {
  int len = strlen(str);
  node->pass++;
  for (int i = 0; i < len; i++) {
    int index = str[i] - 'a';
    if (node->nodes[index] == NULL) {
      node->nodes[index] = newNode();
    }
    node = node->nodes[index];
    node->pass++;
  }
  node->end++;
}

Node* findNodeByStr(Node* root, char* str) {
  int len = strlen(str);
  Node* node = root;
  for (int i = 0; i < len; i++) {
    int index = str[i] - 'a';
    if (node->nodes[index] == NULL) return NULL;
    node = node->nodes[index];
  }
  return node;
}

int count(Node* root, char* str) {
  Node* node = findNodeByStr(root, str);
  return node ? node->end : 0;
}

int prefix(Node* root, char* str) {
  Node* node = findNodeByStr(root, str);
  return node ? node->pass : 0;
}

bool removeNode(Node* root, char* str) {
  if (count(root, str) > 0) {
    int len = strlen(str);
    Node* node = root;
    node->pass--;
    for (int i = 0; i < len; i++) {
      int index = str[i] - 'a';
      Node* subNode = node->nodes[index];
      if (--subNode->pass == 0) {
        freeNode(subNode);
        node->nodes[index] = NULL;
        return true;
      }
      node = node->nodes[index];
    }
    node->end--;
    return true;
  }
  return false;
}

void freeNode(Node* root) {
  if (root == NULL) return;
  for (int i = 0; i < MAXIMUM_LETTER; i++) {
    freeNode(root->nodes[i]);
  }
  free(root);
}

int main() {
#define mem_test
#ifdef mem_test
  while (1) {
#endif
    Node* root = newNode();
    insertNode(root, "hello");
    insertNode(root, "hello");
    insertNode(root, "helo");
    insertNode(root, "helleo");
    insertNode(root, "helwleo");
    assert(count(root, "hello") == 2);
    assert(prefix(root, "hell") == 3);
    assert(prefix(root, "hel") == 5);
    removeNode(root, "hello");
    removeNode(root, "hello");
    removeNode(root, "helo");
    removeNode(root, "helleo");
    removeNode(root, "helwleo");
    freeNode(root);

#ifdef mem_test
  }
#endif
  printf("end\n");
  return 0;
}