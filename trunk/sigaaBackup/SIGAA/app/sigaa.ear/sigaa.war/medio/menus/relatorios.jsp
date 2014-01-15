<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul style="list-style-image: none; list-style: none;">
	<li> Relatórios
		<ul>
			<li>
				<h:commandLink action="#{graficoBimestralNotas.iniciarBusca}"
					value="Relatório Gráfico Bimestral de Notas" onclick="setAba('relatorios')">
				</h:commandLink>
			</li>
		</ul>
	</li>
	<li> Listas
		<ul>
	      	<li> 
	      		<h:commandLink action="#{relatoriosMedio.iniciarRelatorioListaAlunosMatriculados}"
					value="Lista de Alunos Matriculados" onclick="setAba('relatorios')">
				</h:commandLink>
			</li>
	  	</ul>
	  	<ul>
			<li>
				<h:commandLink action="#{fichaDiscenteMedio.iniciarBusca }"
					value="Ficha dos Alunos" onclick="setAba('relatorios')">
				</h:commandLink>
			</li>
		</ul>
	</li>
	<li> Quantitativos
		<ul>
	 		<li>
		 		<h:commandLink	action="#{relatoriosMedio.gerarRelatorioQuantitativoAlunosAnoIngresso}"
					value="Quantitativo de Alunos por Ano de Ingresso" onclick="setAba('relatorios')">
				</h:commandLink>
	 		</li>
	  	</ul>
	</li>
</ul>
