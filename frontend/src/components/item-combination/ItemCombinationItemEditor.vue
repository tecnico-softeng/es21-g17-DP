<template>
  <v-row class="item-combination-item-editor">
    <v-col
      class="item-option"
      v-for="(option, index) in sItem.options"
      :key="index"
      data-cy="itemEditor"
    >
      <v-col>
        <v-checkbox
          v-model="option.correct"
          :label="`${(option.content = sItem0.options[index].content)}`"
          data-cy="itemOption"
          :disabled="index > 0 && sItem.options[0].correct"
          @click="checkNone()"
        >
        </v-checkbox>
      </v-col>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, PropSync, Vue } from 'vue-property-decorator';
import ItemCombinationItem from '@/models/management/questions/ItemCombinationItem';
import ItemCombinationOption from '@/models/management/questions/ItemCombinationOption';

@Component({
  components: {}
})
export default class ItemCombinationItemEditor extends Vue {
  @PropSync('item0', { type: ItemCombinationItem })
  sItem0!: ItemCombinationItem;
  @PropSync('item', { type: ItemCombinationItem })
  sItem!: ItemCombinationItem;

  checkNone() {
    if (this.sItem.options[0].correct)
      this.sItem.options.forEach((option, index) => {
        if (index > 0) option.correct = false;
      });
  }
}
</script>
