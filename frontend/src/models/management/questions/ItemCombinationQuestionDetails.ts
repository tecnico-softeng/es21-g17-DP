import ItemCombinationItem from '@/models/management/questions/ItemCombinationItem';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
  items: ItemCombinationItem[] = [new ItemCombinationItem()];

  constructor(jsonObj?: ItemCombinationQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.items = jsonObj.items.map(
        (item: ItemCombinationItem) => new ItemCombinationItem(item)
      );
    }
  }

  setAsNew(): void {
    this.items.forEach(item => {
      item.id = null;
    });
  }
}
