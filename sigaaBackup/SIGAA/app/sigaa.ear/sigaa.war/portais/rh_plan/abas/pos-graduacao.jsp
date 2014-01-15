<ul>
<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
	<li> Relat�rios Lato-Sensu
		<ul>
		<li><h:commandLink value="Relat�rio de Entradas por Ano" action="#{relatoriosLato.popularEntradasAnoSintetico}" onclick="setAba('pos-graduacao')" /></li>
		<li><h:commandLink value="Relat�rio de Alunos por Curso" action="#{relatoriosLato.alunosCursoSintetico}" onclick="setAba('pos-graduacao')" /></li>
		<li><h:commandLink value="Relat�rio de Cursos por Centro" action="#{relatoriosLato.cursosCentroSintetico}" onclick="setAba('pos-graduacao')" /></li>
		</ul>
	</li>	
</ufrn:checkRole>	
	<li> Relat�rios de Stricto-Sensu
		<ul>
		<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
			<li><h:commandLink action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoMatriculado}" value="Quantitativo de Alunos Ativos / Matriculados" onclick="setAba('pos-graduacao')" id="iniciarQuantitativoMatriculado"/></li>
		</ufrn:checkRole>
		<ufrn:checkRole papeis="<%= new int[] {   SigaaPapeis.PORTAL_PLANEJAMENTO,SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO }  %>">
			<li><h:commandLink value="Relat�rio de Acompanhamento Acad�mico de Servidores que s�o Alunos" action="#{relatorioDesempenhoServidorDiscente.gerarRelatorio}" onclick="setAba('pos-graduacao')" /></li>
		</ufrn:checkRole>			
		</ul>
	</li>	
</ul>