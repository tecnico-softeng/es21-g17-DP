export default class OpenAnswerOption {
    id: number | null = null;
    sequence!: number | null;
    content: string = '';
    correct: boolean = true;
  
    constructor(jsonObj?: OpenAnswerOption) {
      if (jsonObj) {
        this.id = jsonObj.id;
        this.sequence = jsonObj.sequence;
        this.content = jsonObj.content;
        this.correct = jsonObj.correct;
      }
    }
  }
  