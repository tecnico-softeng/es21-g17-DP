import OpenAnswerOption from '@/models/management/questions/OpenAnswerOption';

export default class OpenAnswerItem {
  id: number | null = null;
  sequence: number = 1;
  options: OpenAnswerOption[] = [
    new OpenAnswerOption()
  ];
  content: string = '';

  constructor(jsonObj?: OpenAnswerItem) {
    this.options[0].content = '';

    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.sequence = jsonObj.sequence || this.sequence;
      this.options = jsonObj.options.map(
        (option: OpenAnswerOption) => new OpenAnswerOption(option)
      );

      this.content = jsonObj.content;
    }
  }

  setAsNew(): void {
    this.id = null;
    this.options.forEach(option => {
      option.id = null;
    });
  }
}
