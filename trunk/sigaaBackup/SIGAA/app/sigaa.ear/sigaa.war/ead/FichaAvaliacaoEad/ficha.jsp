<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>

<f:view>

	<c:if test="${ acesso.tutorEad }">
		<%@include file="/portais/tutor/menu_tutor.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema/> &gt; Avaliação Semanal</h2>
	<br>
	
	<h3 style="text-align: center;"> ${ fichaAvaliacaoEad.discente.matricula } - ${ fichaAvaliacaoEad.discente.nome }</h3>

	<c:set var="disciplinas" value="${ fichaAvaliacaoEad.disciplinas }"/>
	
	<c:forEach var="resultado" items="#{ fichaAvaliacaoEad.resultado }" varStatus="loop">
	
		<h4 style="text-align: center; margin-bottom: 5px; cursor: pointer" onclick="$('#unidade${resultado.unidade}').toggle('slow');">${resultado.unidade}<sup>a</sup> Unidade</h4>
		<div id="unidade${resultado.unidade}">
	
			<c:forEach var="avaliacao" items="${ resultado.avaliacoes }" varStatus="loop">
				<table class="listagem">
					<tr>
						<td>
							<table class="subFormulario" width="100%">
								<caption>Semana ${ avaliacao.semana }</caption>
								<thead>
								<tr>
									<th align="center">Itens a Avaliar</th>
									<c:forEach var="disc" items="${ disciplinas }">
										<th width="10%" align="center">${ disc.codigo }</th>
									</c:forEach>
								</tr>	
								</thead>
	
								<tbody>
									<c:forEach var="item" items="${ avaliacao.itens }">
										<tr>
											<td>${ item.nome }</td>
											<c:forEach var="nota" items="${ item.notas }">
												<td align="center"><label class="nota_${ nota.componente.id }"><fmt:formatNumber value="${ nota.nota }" pattern="#0.0"/></label></td>		
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>
								
								<tfoot>
									<tr>
										<td>Nota da Semana: </td>
					
										<c:if test="${ avaliacao.semana <= fichaAvaliacaoEad.metodologia.numeroAulasPrimeiraUnidade || fichaAvaliacaoEad.metodologia.umaProva }">					
											<c:forEach var="media" items="${ avaliacao.medias }">
												<td align="center"><fmt:formatNumber value="${ media }" pattern="#0.0"/></td>
											</c:forEach>					
										</c:if>
										<c:if test="${ avaliacao.semana > fichaAvaliacaoEad.metodologia.numeroAulasPrimeiraUnidade && !fichaAvaliacaoEad.metodologia.umaProva }">
											<c:forEach var="media2" items="${ avaliacao.mediasUnidade2 }">
												<td align="center"><fmt:formatNumber value="${ media2 }" pattern="#0.0"/></td>
											</c:forEach>							
										</c:if>				
									</tr>
									<tr>
										<td colspan="${fn:length(disciplinas) +1 }">
											<p>
												<strong>Observações:</strong><br/>
												${ avaliacao.observacoes }
											</p>					
										</td>
									</tr>
								</tfoot>
							</table>
						</td>
					</tr>
				</table>
			</c:forEach>
		</div>	
		<table class="subFormulario" width="100%">
			<tr>
				<td>
					<strong>Média Final UNIDADE ${resultado.unidade}: </strong>
				</td>
				<c:forEach var="media" items="${ resultado.medias }">
					<td align="center" width="10%"><fmt:formatNumber
						value="${ media }" pattern="#0.0" /></td>
				</c:forEach>
			</tr>
		</table>
	</c:forEach>
	<br/>
	<h:form>
	
	<div align="center">
		<c:if test="${acesso.tutorEad}">
			<h:commandButton value="Cancelar" action="#{fichaAvaliacaoEad.visualizarAvaliacoes}" ></h:commandButton>
		</c:if>
		<c:if test="${acesso.discente}">
			<h:commandButton value="Cancelar" action="#{fichaAvaliacaoEad.voltarVisualizacao}" ></h:commandButton>
		</c:if>
	</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
if (getEl('unidade0')) getEl('unidade0').setVisibilityMode(YAHOO.ext.Element.DISPLAY)
if (getEl('unidade7')) getEl('unidade7').setVisibilityMode(YAHOO.ext.Element.DISPLAY)
</script>