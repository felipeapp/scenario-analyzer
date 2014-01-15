<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Motivo da Solicitação de Alteração</h2>
	
	<h:outputText value="#{processoSeletivo.create}" />
	
	<h:form>
		<table class="formulario" width="80%">
			
			<caption>Dados do Processo Seletivo</caption>
			<tr>
				<th><b>Curso:</b></th>
				<td>
					<c:forEach var="ps" items="#{editalProcessoSeletivo.obj.processosSeletivos}" varStatus="i">
						${ps.curso.unidade.nome} (${ps.curso.nivelDescricao})
						<c:if test="${!i.last}"> , </c:if>
					</c:forEach>
				</td>
			</tr>
			
			<tr>
				<th><b>Período de Inscrições:</b></th>
				<td class="periodo"><ufrn:format type="data"
						name="editalProcessoSeletivo" property="obj.inicioInscricoes" /> a <ufrn:format
						type="data" name="editalProcessoSeletivo" property="obj.fimInscricoes" />
				</td>
			</tr>

			<tr>
			    <td class="subFormulario" colspan="2"><span class="obrigatorio" >Motivo da Solicitação:</span></td>
			</tr>
			
			<tr>
				<td colspan="2" align="center">
					<h:inputTextarea rows="10" cols="80" id="motivo" value="#{editalProcessoSeletivo.obj.motivoAlteracao}"/>
				</td>			
			</tr>
		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{editalProcessoSeletivo.confirmButton}" action="#{editalProcessoSeletivo.solicitarAlteracao}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{editalProcessoSeletivo.cancelar}" />
					</td>
				</tr>
			</tfoot>	
						
		</table>

	</h:form>
	
	<br />
	
	<center>
	  <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>