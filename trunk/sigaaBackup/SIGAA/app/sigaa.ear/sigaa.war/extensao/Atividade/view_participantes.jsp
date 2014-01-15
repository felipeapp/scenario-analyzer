<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>Lista de Participantes de A��es de Extens�o</h2>
		
	<div id="parametrosRelatorio">
		<table class="listagem">
			<caption>Lista de Participantes de A��o de Extens�o</caption>
			<tr>
				<td colspan="3" class="subFormulario">A��o de Extens�o</td>
			</tr>
			<tr>
				<th>C�digo:</th>
				<td style="font-weight: bold;"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.codigo}" id="codigo" /></td>
			</tr>
			<tr>
				<th>T�tulo:</th>
				<td style="font-weight: bold;"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.titulo}" id="titulo" /></td>
			</tr>
			<tr>
				<th>Coordena��o:</th>
				<td style="font-weight: bold;"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.coordenador.pessoa.nome}" id="coord" /></td>
			</tr>
			<tr>
				<th>Per�odo:</th>
				<td style="font-weight: bold;"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataInicio}" id="inicio"> 
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText> 
					at� 
					<h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataFim}" id="fim">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText> 
				</td>
			</tr>
			<tr>
				<td colspan="3" class="subFormulario">Participantes da A��o de Extens�o</td>
			</tr>
		</table>
	</div>
	
	<table class="listagem" width="100%">
		<thead>
			<tr>
				<th style="text-align: right;" width="5%"  >N�</th>
				<th>Nome</th>
				<th>Tipo de Participa��o</th>								
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{atividadeExtensao.atividadeSelecionada.participantesOrdenados}" var="participante" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td align="right"> ${status.count}</td>
					<td>${participante.cadastroParticipante.nome}</td>
					<td>${participante.tipoParticipacao.descricao}</td>
				</tr>			
			</c:forEach>
			<c:if test="${empty atividadeExtensao.atividadeSelecionada.participantesOrdenados}">
                <tr>
                   <td colspan="4"><center><font color="red">N�o h� participantes cadastrados para esta a��o de extens�o.</font></center></td>
                </tr>
            </c:if>
            <tfoot>
				<tr>
					<td colspan="6">
						<center><input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /></center>
					</td>
				</tr>
			</tfoot>
		</tbody>		
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>