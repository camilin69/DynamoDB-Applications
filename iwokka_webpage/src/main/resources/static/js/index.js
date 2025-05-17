let products, stores = []
let currentStore = {}
const ROLE_CLIENT = 'client';
const ROLE_ADMIN = 'admin';

function loadValuesSections() {
    const storesTab = document.getElementById('stores-tab');
    const productsTab = document.getElementById('products-tab');
    
    if (storesTab.classList.contains('active-tab')) {
        loadCategoriesFromStores();
        if(getUserRole() === ROLE_ADMIN) {
            const saveProductButton = document.getElementById('save-product-btn');

            if(!document.getElementById('save-store-button')) {
                const saveStoreButton = document.createElement('button');
                saveStoreButton.textContent = 'Add New Store'; 
                saveStoreButton.id = 'save-store-button';
                saveStoreButton.addEventListener('click', () => {
                    const saveStoreSection = document.getElementById('stores-section-save');
                    saveStoreSection.style.display = saveStoreSection.style.display === 'none' ? 'block' : 'none';
                });
                document.getElementById("stores-section-header").appendChild(saveStoreButton);
            }
            
            saveProductButton.addEventListener('click', () => {
                const saveProductSection = document.getElementById('product-section-save');
                saveProductSection.style.display = saveProductSection.style.display === 'none' ? 'block' : 'none';
            });
            
        }

    }else if (productsTab.classList.contains('active-tab')) {
        loadLabelsFromProducts();
    }
  
}

function loadCategoriesFromStores() {
    const select = document.getElementById('find-by-categories');

    fetch('/api/stores/findAllUniqueCategories')
    .then(response => {
        if (!response.ok) throw new Error('Error getting all unique categories for stores');

        return response.json();
    })
    .then(categories => {
        select.innerHTML = '<option value="all" selected>Select a category</option>';
        
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            select.appendChild(option);
        });
        
        select.addEventListener('change', (event) => {
            loadStoresByCategory(event.target.value);
        });
        loadStoresByCategory('all');
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function loadStoresByCategory(category) {
    const url = category === 'all' 
        ? '/api/stores/findAll' 
        : `/api/stores/findAllByCategory?category=${encodeURIComponent(category)}`;

    fetch(url)
    .then(response => {
        if (!response.ok) throw new Error('Error getting stores');
        return response.json();
    })
    .then(stores => {
        const container = document.getElementById('stores-container');
        container.innerHTML = '';
        const isAdmin = getUserRole() === ROLE_ADMIN;
        stores.forEach(store => {
            const adminButtons = isAdmin ? `
                <div class="admin-actions mt-2">
                    <button class="btn btn-sm btn-primary edit-store" data-id="${store.category}">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="btn btn-sm btn-danger delete-store" data-id="${store.category}">
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            ` : '';
            
            const storeCard = `
                <div class="col-md-4 mb-4">
                    <div class="card store-card">
                        <div class="card-body">
                            <h5 class="card-title">${store.label}</h5>
                            <p class="card-text">
                                <strong>Category:</strong> ${store.category}<br>
                                <strong>Label:</strong> ${store.label}<br>
                                <strong>Clients:</strong> ${store.clients?.length || 0}<br>
                                <strong>Products:</strong> ${store.products?.length || 0}
                            </p>
                            <button class="btn btn-sm btn-info view-products" 
                                    data-store='${JSON.stringify(store)}'>
                                <i class="fas fa-eye"></i> Ver Productos
                            </button>
                            ${adminButtons}
                        </div>
                    </div>
                </div>`;
            container.insertAdjacentHTML('beforeend', storeCard);
        });

        document.querySelectorAll('.view-products').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.target.dataset.store);
                currentStore = store;
                showProductsModal(store);
            });
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function showProductsModal(store) {
    const modal = new bootstrap.Modal(document.getElementById('productsModal'));
    const modalTitle = document.getElementById('productsModalLabel');
    const tableBody = document.getElementById('productsTableBody');
    const addProductBtn = document.getElementById('save-product-btn');
    const productFormSection = document.getElementById('product-section-save');
    
    modalTitle.textContent = `Products from ${store.label}`;
    tableBody.innerHTML = '';
    productFormSection.style.display = 'none';
    
    const isAdmin = getUserRole() === ROLE_ADMIN;
    addProductBtn.style.display = isAdmin ? 'block' : 'none';
    
    if(store.products && store.products.length > 0) {
        store.products.forEach(product => {
            createProductRow(product, store, isAdmin, tableBody);
        });
    } else {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No hay productos disponibles</td></tr>';
    }
    
    modal.show();
    
    // Evento para añadir nuevo producto
    if(isAdmin) {
        addProductBtn.addEventListener('click', () => {
            productFormSection.style.display = 'block';
            addProductBtn.style.display = 'none';
            // Resetear formulario
            document.getElementById('label-product-register').value = '';
            document.getElementById('name-product-register').value = '';
            document.getElementById('description-product-register').value = '';
            document.getElementById('price-product-register').value = '';
        });
    }
}

function createProductRow(product, store, isAdmin, tableBody) {
    const row = document.createElement('tr');
    row.dataset.productLabel = product.label;
    
    if(product.editing) {
        // Edition Mode
        row.innerHTML = `
            <td>
                <input type="text" class="form-control form-control-sm" value="${product.label}" id="edit-label-${product.label}">
            </td>
            <td>
                <input type="text" class="form-control form-control-sm" value="${product.name}" id="edit-name-${product.label}">
            </td>
            <td>
                <textarea class="form-control form-control-sm" id="edit-description-${product.label}" 
                    rows="2">${product.description}</textarea>
            </td>
            <td>
                <input type="number" step="0.01" class="form-control form-control-sm" 
                    value="${product.price.toFixed(2)}" id="edit-price-${product.label}">
            </td>
            <td>
                <button class="btn btn-sm btn-success save-edit" data-id="${product.label}">
                    <i class="fas fa-save"></i> Save
                </button>
                <button class="btn btn-sm btn-secondary cancel-edit" data-id="${product.label}">
                    <i class="fas fa-times"></i> Cancel
                </button>
            </td>
        `;
    } else {
        // Normal visualization
        row.innerHTML = `
            <td>${product.label}</td>
            <td>${product.name}</td>
            <td>${product.description}</td>
            <td>$${product.price.toFixed(2)}</td>
            <td>
                ${isAdmin ? `
                <button class="btn btn-sm btn-warning edit-product" data-id="${product.label}">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger delete-product" data-id="${product.label}">
                    <i class="fas fa-trash"></i>
                </button>
                ` : 'No Available'}
            </td>
        `;
    }
    
    tableBody.appendChild(row);
    
    if(isAdmin && !product.editing) {
        // Add events only if is not in edition mode
        row.querySelector('.edit-product')?.addEventListener('click', () => {
            product.editing = true;
            createProductRow(product, store, isAdmin, tableBody);
        });
        
        row.querySelector('.delete-product')?.addEventListener('click', () => {
            if(confirm(`¿Eliminar producto ${product.label}?`)) {
                deleteProduct(store.category, store.label, product.label);
            }
        });
    }
    
    if(product.editing) {
        // Add events only if is in edit mode
        row.querySelector('.save-edit')?.addEventListener('click', () => {
            const updatedProduct = {
                label: document.getElementById(`edit-label-${product.label}`).value,
                name: document.getElementById(`edit-name-${product.label}`).value,
                description: document.getElementById(`edit-description-${product.label}`).value,
                price: parseFloat(document.getElementById(`edit-price-${product.label}`).value),
                originalLabel: product.label // Guardamos el label original para identificar el producto
            };
            saveEditedProduct(store.category, store.label, updatedProduct);
        });
        
        row.querySelector('.cancel-edit')?.addEventListener('click', () => {
            product.editing = false;
            createProductRow(product, store, isAdmin, tableBody);
        });
    }
}

function loadLabelsFromProducts () {
    const select = document.getElementById('find-by-labels');

    fetch('/api/products/findAllUniqueLabels')
    .then(response => {
        if (!response.ok) throw new Error('Error getting all unique labels for products');
        return response.json();
    })
    .then(labels => {
        select.innerHTML = '<option value="all" selected>Select a label</option>';
        
        labels.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            select.appendChild(option);
        });

        select.addEventListener('change', (event) => {
            loadProductsByLabel(event.target.value);
        });
        
        loadProductsByLabel('all');
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function loadProductsByLabel (label) {
    const url = label === 'all' 
        ? '/api/products/findAll' 
        : `/api/products/findAllByLabel?label=${encodeURIComponent(label)}`;

    fetch(url)
    .then(response => {
        if (!response.ok) throw new Error('Error getting products');
        return response.json();
    })
    .then(products => {
        const container = document.getElementById('products-container');
        container.innerHTML = '';
        
        products.forEach(product => {
            const productCard = `
                <div class="col-md-4">
                    <div class="card product-card">
                        <div class="card-body">
                            <h5 class="card-title">Products from ${product.label}</h5>
                            <p class="card-text">
                                <strong>Name:</strong> ${product.name}<br>
                                <strong>Description:</strong> ${product.description}<br>
                                <strong>Price:</strong> ${product.price}<br>
                            </p>
                        </div>
                    </div>
                </div>`;
            container.insertAdjacentHTML('beforeend', productCard);
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function saveStore (event) {
    event.preventDefault();
    const storeData = {
        category : document.getElementById('category-store-register').value,
        label : document.getElementById('label-store-register').value,
        products : [],
        clients : []
    };

    fetch('http://localhost:8080/api/stores/save', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(storeData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json()
    })
    .then(store => {
        event.target.reset();
        stores.push(store);
        console.log(store);
        loadCategoriesFromStores();
    })
    .catch(error => {
        console.error('Error Storing: ', error);
    });
    
}

function saveProduct (event) {
    event.preventDefault();
    const newStore = {};
    const productData = {
        label : document.getElementById('label-product-register').value,
        name : document.getElementById('name-product-register').value,
        description : document.getElementById('description-product-register').value,
        price : document.getElementById('price-product-register').value
    };

    //ADD TO THE STORE
    fetch(`http://localhost:8080/api/stores/updateProducts?category=${currentStore.category}&label=${currentStore.label}`, {
        method : 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(productData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json();
    })
    .then(store => {
        console.log(store);
        newStore = store;

    })
    .catch(error => {
        console.error('Error saving product to store: ', error);
    });

    //ADD TO PRODUCTS TABLE
    fetch('http://localhost:8080/api/products/save', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(productData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json();
    })
    .then(product => {
        console.log(product);
        loadStoresByCategory(document.getElementById('find-by-categories').value);
        showProductsModal(newStore);
    })
    .catch(error => {
        console.error('Error Saving Products: ', error);
    });
}

function showSection(sectionId) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    document.getElementById(`${sectionId}-section`).classList.add('active');
    
    document.querySelectorAll('.nav-link').forEach(tab => {
        tab.classList.remove('active-tab');
    });
    document.getElementById(`${sectionId}-tab`).classList.add('active-tab');
    loadValuesSections();
}

function getUserRole() {
    const userData = localStorage.getItem('user');
    if (!userData) return null;

    try {
        if (typeof userData === 'object') return userData.role;
        
        const user = typeof userData === 'string' ? JSON.parse(userData) : userData;
        console.log("User data:", user);
        return user?.role || null;
    } catch (e) {
        console.error('Error parsing user data:', e);
        return null;
    }
}

function closeSidebar() {
    document.getElementById('store-products-sidebar').style.width = '0';
    document.getElementById('product-stores-sidebar').style.width = '0';
}


function logout() {
    localStorage.clear();
    window.location.href = '/';
}

loadValuesSections();