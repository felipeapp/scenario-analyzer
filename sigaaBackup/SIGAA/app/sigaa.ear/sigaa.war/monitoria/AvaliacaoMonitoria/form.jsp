<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/javascript/avaliacao_monitoria.js"></script>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Avaliar Projeto de Ensino</h2>
	
	<h:form>
	<h:messages showDetail="true"/>
	
	<table class="formulario" width="100%">
		<caption>Avaliar Projeto de Ensino</caption>	
		
		<tr>
			<td colspan="2">
				<table width="100%" class="subFormulario">
					<tr>
						<td>
							<b>Título: </b><h:outputText value="#{avalProjetoMonitoria.obj.projetoEnsino.titulo}" id="projetoAvaliado"/>
						</td>
					</tr>
					<tr>
						<td>
							<b>Tipo: </b>&nbsp;&nbsp;<h:outputText value="#{avalProjetoMonitoria.obj.projetoEnsino.tipoProjetoEnsino.descricao}" id="projetoTipo"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	
	
		<c:forEach items="${ avalProjetoMonitoria.grupos }" var="grupo" varStatus="status">
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption>${status.count} - ${ grupo.denominacao }</caption>
						<thead>
							<tr>
								<td width="80%">Item</td>
								<td width="10%" style="text-align: right ;">Nota</td>
								<td width="10%" style="text-align: right ;">Máximo</td>
							</tr>
						</thead>
					
						<c:forEach items="${ grupo.notas }" var="nota" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					
								<td>${status.count}.${loop.count} - ${ nota.itemAvaliacao.descricao }</td>
						
								<td style="text-align: right;">

									<input  type="text"
							   				name="nota_${ grupo.id }_${ loop.index }"
											maxlength="4"
											size="5"
											onkeyup="calculaTotal(${ grupo.id })"
											onkeydown="return(formataValor(this, event, 2))"										
											value="<fmt:formatNumber pattern="#0.00" value="${ nota.nota > 0.0 ? nota.nota : null }"/>" 
											class="nota_${ grupo.id } nota"											
									/>

								</td>
						
								<td style="text-align: right;">
									<fmt:formatNumber pattern="#0.00" value="${ nota.itemAvaliacao.notaMaxima }"/>
								</td>
						
							</tr>
						</c:forEach>
									
						<tr>
							<td align="right"><strong>Total: </strong></td>
							<td align="center"><strong><span id="total_${ grupo.id }"><fmt:formatNumber pattern="#0.00" value="${ grupo.totalGrupo }"/></span></strong></td>
							<td></td>
						</tr>
					</table>
				<br/>&nbsp;
			</td>
		</tr>
	</c:forEach>
	
	<tr bgcolor="#C8D5EC">
		<td colspan="2" align="center"><strong>Total: <span id="total"><fmt:formatNumber pattern="#0.00" value="${ avalProjetoMonitoria.obj.notaAvaliacao }"/></span></strong></td>
	</tr>
	
	<tr>
		<td>
		Parecer: <br/>
		<h:inputTextarea value="#{avalProjetoMonitoria.obj.parecer}" style="width:98%" rows="4" id="parecer"/></td>
	</tr>
	<tr><td colspan="2"></td></tr>
	<tfoot>
		<tr>
			<td colspan="2">
			<h:commandButton value="Avaliar Projeto"action="#{avalProjetoMonitoria.avaliar}" />
			<h:commandButton value="Cancelar" action="#{avalProjetoMonitoria.cancelar}" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>
	
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
<c:forEach items="${ avalProjetoMonitoria.grupos }" var="grupo" varStatus="status">
	calculaTotal(${ grupo.id });
</c:forEach>
</script>