document.addEventListener('DOMContentLoaded', function () {
    try {
        const ctx = document.getElementById('linhaDespesas');
        if (!ctx) {
            console.error('Canvas não encontrado');
            return;
        }

        const hoje = new Date();
        const nomeMes = hoje.toLocaleString('pt-PT', { month: 'short' });
        const labels = [1, 3, 7, 10, 12, 15].map(dia => `${dia} ${nomeMes}`);
        const despesas = [450, 70, 130, 30, 7, 28];

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Despesas (€)',
                    data: despesas,
                    fill: false,
                    borderColor: 'red',
                    tension: 0.3,
                    pointBackgroundColor: 'red',
                    pointRadius: 5
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    datalabels: {
                        color: 'black',
                        anchor: 'end',
                        align: 'top',
                        font: {
                            weight: 'bold'
                        },
                        formatter: function (valor) {
                            return `€${valor}`;
                        }
                    },
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Evolução das Despesas em ' + nomeMes.charAt(0).toUpperCase() + nomeMes.slice(1)
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            },
            plugins: [ChartDataLabels]
        });
    } catch (error) {
        console.error('Erro ao criar o gráfico:', error);
    }
});
