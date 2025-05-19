import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Category } from '../entities/category';
import { CategoryService } from '../category.service';
import { HttpErrorResponse } from '@angular/common/http';
import { DashboardComponent } from '../dashboard/dashboard.component';


@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent{

  @Input() category!: Category;
  @Input() expanded: boolean = false;
  @Output() expandClicked = new EventEmitter<number>();
  @Output() deleteClicked = new EventEmitter<any>();

  isEditing = false;
  editedName = '';

  constructor(private categoryService: CategoryService) {}

  onClose(event: MouseEvent) {
    event.stopPropagation(); 
    this.expandClicked.emit(-1);
  }

  startEdit(event: MouseEvent) {
    event.stopPropagation();
    this.isEditing = true;
    this.editedName = this.category.name;
  }

  cancelEdit(event: MouseEvent) {
    event.stopPropagation();
    this.isEditing = false;
  }

  saveEdit(event: Event) {
    event.preventDefault();
    if (!this.editedName.trim()) return;

    const updatedCategory = { ...this.category, name: this.editedName };

    this.categoryService.updateCategory(this.category.id, updatedCategory).subscribe({
      next: () => {
        this.category.name = this.editedName;
        this.isEditing = false;
      },
      error: (error: HttpErrorResponse) => {
        alert('Error while saving: ' + error.message);
      }
    });
  }

  confirmDelete(event: MouseEvent) {
    event.stopPropagation();
    const confirmed = confirm(`Are you sure you want to delete the category "${this.category.name}"?`);
    if (confirmed) {
      this.categoryService.deleteCategory(this.category.id).subscribe({
        next: () => {
          this.deleteClicked.emit(this.category.id);
          this.expandClicked.emit(-1); 
        },
        error: (error: HttpErrorResponse) => {
          alert('Error while deleting: ' + error.message);
        }
      });
    }
  }
}

