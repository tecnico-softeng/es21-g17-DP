<template>
  <div>
    <v-select
      :items="languages"
      v-model="sQuestionDetails.language"
      label="Language"
      :disabled="readonlyEdit"
    />
    <div class="code-create">
      <v-card-actions>
        <v-spacer />
        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn color="primary" small @click="Dropdownify" v-on="on">
              Answer Slot
            </v-btn>
          </template>
          <span>
            Select code on the editor and click here to create a fillable space.
          </span>
        </v-tooltip>
      </v-card-actions>

      <FillInOptions
        v-for="(item, index) in sQuestionDetails.fillInSpots"
        :key="index"
        :option="item"
        v-model="sQuestionDetails.fillInSpots[index]"
      />
      <!-- v-on:change="handleInput" -->
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';
import Question from '@/models/management/Question';
//import FillInOptions from '@/components/code-fill-in/OpenAnswerOptions.vue';
//import Option from '@/models/management/Option';
//import OpenAnswerSpot from '@/models/management/questions/OpenAnswerSpot';

@Component({
  components: {
  }
})
export default class OpenAnswerQuestionEdit extends Vue {
  @PropSync('questionDetails', { type: OpenAnswerQuestionDetails })
  sQuestionDetails!: OpenAnswerQuestionDetails;
  @Prop({ default: true }) readonly readonlyEdit!: boolean;
  counter: number = 1;
  created() {
    //this.counter = this.getMaxDropdown();
  }
  /*getMaxDropdown() {
    return (
      (Math.max.apply(
        Math,
        this.sQuestionDetails.fillInSpots.map(function(o) {
          return o.sequence;
        })
      ) |
        0) +
      1
    );
  }
  onCmCodeChange(newCode: string) {
    this.sQuestionDetails.code = newCode;
  }*/
  /*Dropdownify() {
    const content = this.baseCodeEditorRef.codemirror.getSelection();
    if (content) {
      const option = new Option();
      option.correct = true;
      option.content = content;
      const item = new CodeFillInSpot();
      item.options = [option];
      item.sequence = this.counter;
      this.sQuestionDetails.fillInSpots.push(item);
      this.baseCodeEditorRef.codemirror.replaceSelection(
        '{{slot-' + this.counter + '}}'
      );
      this.counter++;
    }
  }*/
}
</script>

<style>
.cm-custom-drop-down {
  background: #ffa014;
  color: white;
  font-size: x-small;
  padding: 4px 2px 4px 2px;
  border-radius: 5px;
  font-weight: bolder;
  height: 16px;
}
.code-create {
  text-align: left;
}
.CodeMirror-linenumber.CodeMirror-gutter-elt {
  left: 0;
}
</style>
