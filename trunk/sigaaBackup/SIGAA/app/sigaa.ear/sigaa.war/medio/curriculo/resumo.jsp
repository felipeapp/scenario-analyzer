<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2 class="title">Estrutura Curricular &gt; Resumo</h2>

	<h:form id="formulario">

		<table class="formulario" width="700px">
			<caption class="formulario">Dados da Estrutura Curricular</caption>
			<tr>
				<th class="rotulo" width="50%">Código do Currículo: </th>
				<td><h:outputText value="#{curriculoMedio.obj.codigo }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Curso: </th>
				<td><h:outputText value="#{curriculoMedio.obj.cursoMedio.nome }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Série: </th>
				<td><h:outputText
					value="#{curriculoMedio.obj.serie.descricaoCompleta}" /></td>
			</tr>
			<tr>
				<th class="rotulo">Carga Horária Total:</th>
				<td><h:outputText value="#{curriculoMedio.obj.cargaHoraria}" /></td>
			</tr>
			
			<tr>
				<th class="rotulo">Ano de Entrada em Vigor::</th>
				<td><h:outputText value="#{curriculoMedio.obj.anoEntradaVigor}" /></td>
			</tr>
			<tr>
				<th class="rotulo">Prazo de Conclusão: </th>
				<td><h:outputText value="#{curriculoMedio.obj.unidadeTempo.descricao}"/></td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="listagem" width="100%" >
					<caption class="listagem" style="text-align: center;">Disciplinas</caption>
					<c:set value="0" var="ch" />
					<c:if test="${not empty curriculoMedio.obj.curriculoComponentes}">
					<c:forEach var="linha" items="#{curriculoMedio.obj.curriculoComponentes}" varStatus="status">
						<c:set value="${ch + linha.chAno }" var="ch" />
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${linha.descricaoComponente} h</td>
						</tr>		
					</c:forEach>
					</c:if>
					<c:if test="${empty curriculoMedio.obj.curriculoComponentes}">
						<tr><td align="center">Nenhuma Disciplina Vinculada a Estrutura Curricular.</td></tr> 
					</c:if>
					
					<tr style="background-color: #EFEBDE">
						<td><b>CH Total da Disciplinas:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
					</tr>
					
				</table>
				</td>					
			</tr>	
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="hidden" value="${curriculoMedio.obj.id}" name="id">
						<c:choose>
							<c:when test="${curriculoMedio.confirmButton == 'Ativar'}">
								<h:commandButton value="Ativar" action="#{curriculoMedio.cadastrar}" id="btnConfirmar"/>
								<h:commandButton value="<<Voltar" action="#{curriculoMedio.listar}" id="btnvoltar"/>
							</c:when>
							<c:when test="${curriculoMedio.confirmButton == 'Inativar'}">
								<h:commandButton value="Inativar" action="#{curriculoMedio.cadastrar}" id="btnConfirmar"/>
								<h:commandButton value="<<Voltar" action="#{curriculoMedio.listar}" id="btnvoltar"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Confirmar" action="#{curriculoMedio.cadastrar}" id="btnConfirmar"/>
								<h:commandButton value="<<Voltar" action="#{curriculoMedio.voltar}" id="btnvoltar"/>
							</c:otherwise>
						</c:choose>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{curriculoMedio.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio" style="width: 60%"> Campos de preenchimento obrigatório. </div>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
