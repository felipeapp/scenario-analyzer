<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina"><html:link
	action="/ensino/tecnico/modulo/wizard?dispatch=cancelar">
	<ufrn:subSistema semLink="true" />
</html:link> &gt; ${((moduloTecnicoForm.obj.id == 0)?'Cadastro de ':'Atualiza��o de')} 
<ufrn:subSistema teste="tecnico">M�dulo</ufrn:subSistema> 
<ufrn:subSistema teste="not tecnico">S�rie</ufrn:subSistema></h2>

<html:form action="/ensino/tecnico/modulo/wizard" method="post">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.unidade.id"
		value="${ sessionScope.usuario.unidade.id }" />
	<table class="formulario" width="99%">
		<caption>Informa��es <ufrn:subSistema teste="tecnico"> do M�dulo</ufrn:subSistema>
		<ufrn:subSistema teste="not tecnico"> da S�rie</ufrn:subSistema></caption>
			<tr>
				<th class="required">Descri��o:</th>
				<td colspan="3"><html:text property="obj.descricao"
					maxlength="100" size="100" onkeyup="CAPS(this)" /></td>
			</tr>

			<tr>
				<th class="required">Carga Hor�ria Total:</th>
				<td width="80"><html:text property="obj.cargaHoraria"
					maxlength="4" size="4" /></td>
			</tr>
			<tr>
				<th>Carga Hor�ria Complementar:</th>
				<td>
					<input type="text" value="${moduloTecnicoForm.obj.cargaHoraria - moduloTecnicoForm.obj.chDisciplinas}" size="4" disabled="disabled" />  
					<ufrn:help>
						Campo calculado automaticamente. Consiste da diferen�a entre a carga hor�ria total do m�dulo informada e 
						a soma das cargas hor�rias das disciplinas do m�dulo.
					</ufrn:help>
				</td>
			</tr>
			<!-- lista de disciplinas adicionadas -->
			<tr>
				<td colspan="2" class="subFormulario">
					Adicione Disciplinas <ufrn:subSistema
						teste="tecnico"> ao M�dulo</ufrn:subSistema> <ufrn:subSistema
						teste="not tecnico"> � S�rie</ufrn:subSistema>
				</td>
			</tr>
			<tr>
				<th>Disciplina:</th>
				<td><c:set var="idAjax"
					value="moduloDisciplina.disciplina.id" /> <c:set var="nomeAjax"
					value="moduloDisciplina.disciplina.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/disciplina.jsp"%>
				</td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<html:button dispatch="addDisciplina" value="Adicionar Disciplina" style="height: 30px;" />
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<c:if test="${ not empty moduloTecnicoForm.obj.moduloDisciplinas }">
						<div class="infoAltRem"><html:img page="/img/delete.gif"
							style="overflow: visible;" /> : Retirar Disciplina</div>
						<table class="listagem" width="95%">
							<thead>
								<tr>
									<td width="10%">CH</td>
									<td align="left">Disciplinas Adicionadas</td>
									<td width="5"></td>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${ moduloTecnicoForm.obj.moduloDisciplinas }"
									var="md">
									<tr>
										<td>${ md.disciplina.chTotal } h</td>
										<c:set var="chTotal"
											value="${chTotal + md.disciplina.chTotal}" />
										<td align="left">${ md.disciplina.codigo } - ${
										md.disciplina.nome }</td>
										<td><html:link
											action="/ensino/tecnico/modulo/wizard?dispatch=remDisciplina&disciplinaId=${md.disciplina.id}&mdId=${md.id}">
											<html:img page="/img/delete.gif" style="border:none"
												title="Retirar essa disciplina" />
										</html:link></td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td style="font-weight: bold;" align="center">
										Total: ${chTotal} h
									</td>
									<td colspan="2"></td>
								</tr>
							</tfoot>
						</table>
					</c:if>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2"><html:button dispatch="chamaModelo" value="${ operacao }" />
				<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/></td>
			</tr>
		</tfoot>
	</table>
</html:form>

<br />
<div class="obrigatorio"> Campos de preenchimento obrigat�rio.</div>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>