	<ul>
		<li> Relatórios
		  <ul>
			<li> <h:commandLink value="Relatório de todas as atividades" action="#{todaProducao.exibirOpcoes}" onclick="setAba('relatorios')"/> </li>
			<li> <h:commandLink value="Quantitativos de produção acadêmica" action="#{prodQuantitativo.verFormulario}" onclick="setAba('relatorios')"/> </li>
			<li> <h:commandLink value="Relatório de Avaliação para Concessão de Cotas" action="#{producao.verRelatorioCotas}" onclick="setAba('relatorios')"/> </li>
			<li> <h:commandLink value="Relatório de Produtividade Docente (Antigo GED)" action="#{producao.verRelatorioProgressao}" onclick="setAba('relatorios')"/> </li>
		  </ul>
	</ul>
