import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationItemStatement from '@/models/statement/questions/ItemCombinationItemStatement';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
  items: ItemCombinationItemStatement[] = [];

  constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
    super(QuestionTypes.ItemCombination);
    if (jsonObj) {
      this.items = jsonObj.items
        ? jsonObj.items.map(
            (item: ItemCombinationItemStatement) =>
              new ItemCombinationItemStatement(item)
          )
        : this.items;
    }
  }
}
