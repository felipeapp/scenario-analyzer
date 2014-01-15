<ul>
<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
	<li> Relatórios Lato-Sensu
		<ul>
		<li><h:commandLink value="Relatório de Entradas por Ano" action="#{relatoriosLato.popularEntradasAnoSintetico}" onclick="setAba('pos-graduacao')" /></li>
		<li><h:commandLink value="Relatório de Alunos por Curso" action="#{relatoriosLato.alunosCursoSintetico}" onclick="setAba('pos-graduacao')" /></li>
		<li><h:commandLink value="Relatório de Cursos por Centro" action="#{relatoriosLato.cursosCentroSintetico}" onclick="setAba('pos-graduacao')" /></li>
		</ul>
	</li>	
</ufrn:checkRole>	
	<li> Relatórios de Stricto-Sensu
		<ul>
		<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
			<li><h:commandLink action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoMatriculado}" value="Quantitativo de Alunos Ativos / Matriculados" onclick="setAba('pos-graduacao')" id="iniciarQuantitativoMatriculado"/></li>
		</ufrn:checkRole>
		<ufrn:checkRole papeis="<%= new int[] {   SigaaPapeis.PORTAL_PLANEJAMENTO,SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO }  %>">
			<li><h:commandLink value="Relatório de Acompanhamento Acadêmico de Servidores que são Alunos" action="#{relatorioDesempenhoServidorDiscente.gerarRelatorio}" onclick="setAba('pos-graduacao')" /></li>
		</ufrn:checkRole>			
		</ul>
	</li>	
</ul>