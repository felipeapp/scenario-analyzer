<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<a4j:keepAlive beanName="tutoriaIMD"/>
<f:view>
	<h:form>
		
		<c:if test="${(not empty tutoriaIMD.discentesTurma)}">
		
			<p align="center"><h2 align="center">LISTAGEM DE DISCENTES<br />TURMA: ${tutoriaIMD.turmaEntradaSelecionada.anoReferencia}.${tutoriaIMD.turmaEntradaSelecionada.periodoReferencia} - ${tutoriaIMD.turmaEntradaSelecionada.especializacao.descricao} - ${tutoriaIMD.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${tutoriaIMD.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}<br /><br /></p>
			
			<table class="tabelaRelatorio" style="width:100%">
				<caption>Listagem de Alunos (${tutoriaIMD.qtdDiscentes})</caption>
				
				<thead>
					<tr>
						<th>Matrícula</th>
						<th>Discente</th>
					</tr>
				</thead>
				
				<c:forEach items="#{tutoriaIMD.discentesTurma}" var="item" varStatus="status">
					<tr style="background-color: ${status.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}" align="right">
					
						<td>${item.discente.matricula}</td>
						<td>${item.discente.nome}</td>
			
					</tr>
				</c:forEach>
				
			</table>
		</c:if>
		<c:if test="${(empty tutoriaIMD.discentesTurma)}">
			<center><i> Nenhum discente encontrado.</i></center>
		</c:if>
	</h:form>

</f:view>



<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>