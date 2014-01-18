<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Relat�rio de frequ�ncia por Discente > Sele��o do Discnete</h2> 
<a4j:keepAlive beanName="lancamentoFrequenciaIMD"/>
<f:view>
	<p align="center"><h2 align="center">TURMA: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.anoReferencia}.${lancamentoFrequenciaIMD.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.cursoTecnico.nome}
	<br />OP��O P�LO GRUPO: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
	
	<c:if test="${not empty lancamentoFrequenciaIMD.listaDiscentesTurma}">
			
		<table class=listagem style="width:100%">
		
			<div class="infoAltRem">
			    <h:graphicImage url="/img/seta.gif"/>: Selecionar Discente
			</div>
		
			<caption class="listagem">Sele��o do Discente IMD </caption>
			
			<thead>
				<tr>
					<td>Matr�cula</td>
					<td>Discente</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{lancamentoFrequenciaIMD.listaDiscentesTurma}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				
					<td>${item.discente.matricula}</td>
					<td>${item.discente.nome}</td>
					
					<td width=25>
						<h:commandLink action="#{lancamentoFrequenciaIMD.salvarDiscente}"  >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar Discente" alt="Selecionar Discente"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</table>
	</c:if>
	<c:if test="${empty lancamentoFrequenciaIMD.listaDiscentesTurma}">
		<p align="center">Nenhum discente encontrado para a turma selecionada.	</p>
	</c:if>

</f:view>



<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>