import { Component, OnInit } from '@angular/core';
import { Category } from '../entities/category';
import { CategoryService } from '../category.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit{

  constructor(private categoryService: CategoryService, private router: Router) {}

  categories: Category[] = [];

  newCategoryName: string = '';

  showForm = false;

  expandedCategoryId: number | null = null;

  ngOnInit() {
    this.categoryService.getCategories().subscribe(data => this.categories = data);
  }

  addCategory() {
    if (!this.newCategoryName.trim()) return;
  
    const category = { name: this.newCategoryName } as Category;
  
    this.categoryService.addCategory(category).subscribe({
      next: (createdCat) => {
        this.categories.push(createdCat);
        this.newCategoryName = '';
        this.showForm = false; 
      },
      error: (err) => {
        console.error('Failed to add category:', err);
      }
    });
  }

  logout(): void {
    localStorage.removeItem('jwt-token'); 

    this.router.navigate(['/login']);
  }

  accountMenagement(): void {
    this.router.navigate(['/account'])
  }

  onCategoryExpand(categoryId: number) {
    if (categoryId === -1) {
      this.expandedCategoryId = null;
      return;
    }
  
    if (this.expandedCategoryId !== null) return;
  
    this.expandedCategoryId = categoryId;
  }

  anyCategoryExpanded(): boolean {
    return this.expandedCategoryId !== null;
  }
  
  collapseAll() {
    this.expandedCategoryId = null;
  }

  onCategoryDeleted(categoryId: number) {
    this.categories = this.categories.filter(c => c.id !== categoryId);
  
    if (this.expandedCategoryId === categoryId) {
      this.expandedCategoryId = null;
    }
  }

}
