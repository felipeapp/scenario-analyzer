<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurmaMedioMBean.descricaoTipo} &gt; Definir Turma de Destino</h2>
	<h:form>
		<table class="visualizacao" style="width:95%;"> 
			<caption>Turma de Origem</caption>
			<tbody>
			<tr>
				<th width="50%">Turma:</th>
				<td> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.descricaoCompleta} </td>
			</tr>
			<tr>
				<th>Ano:</th>
				<td> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.ano}	</td>
			</tr>
			<tr>
				<th>Turno:</th>
				<td> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.turno.descricao} </td>
			</tr>
			<tr>
				<th>Capacidade da Turma:</th>
				<td> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.capacidadeAluno} </td>
			</tr>
			<tr>
				<th>Alunos Matriculados:</th>
				<td> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.qtdMatriculados} </td>
			</tr>
			</tbody>
		</table>
	</h:form>
		
	<br />
	<c:if test="${not empty transferenciaTurmaMedioMBean.turmasDestino}">
		<br>
		<center>
		<div class="infoAltRem" style="width: 95%;"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
		Selecionar esta Turma<br />
		</div>
		</center>
		<table class=listagem style="width:95%;">
			<caption class="listagem">Selecione a Turma de Destino</caption>
			<thead>
			<tr>
				<td width="50%">Turma</td>
				<td align="center">Ano</td>
				<td >Turno</td>
				<td style="text-align: right;">Matriculados</td>
				<td style="text-align: right;">Capacidade</td>
				<td></td>
			</tr>
			</thead>
			<h:form>
			
				<c:forEach items="#{transferenciaTurmaMedioMBean.turmasDestino}" var="item" varStatus="linha">
					<tr class="${ linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> ${item.serie.descricaoCompleta} - ${item.nome} </td>
						<td> ${item.ano} </td>
						<td> ${item.turno.descricao}</td>
						<td align="right"> ${ item.qtdMatriculados } </td>
						<td align="right"> ${ item.capacidadeAluno } </td>
						<td width="2%">
							<h:commandLink action="#{transferenciaTurmaMedioMBean.selecionarTurmaDestino}">
									<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" title="Selecionar Turma"/>
									<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</h:form>
		</table>
	</c:if>
	<br>
	<center>
		<h:form>
		<h:commandButton value="<< Voltar" action="#{transferenciaTurmaMedioMBean.voltarTurmaOrigem}" />
		<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurmaMedioMBean.cancelar}" />
		</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>