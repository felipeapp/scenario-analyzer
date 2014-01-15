<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<f:view>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>	
	
	<h2><ufrn:subSistema /> &gt; Consolidação da Auto-Validação de Produções Intelectuais</h2>
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b> </p>
		
		<p>
			Esta operação destina-se a consolidar as produções intelectuais cadastradas e validadas pelos docentes.
		</p>
		<p>
			Por favor, selecione abaixo o docente cujas produções deverão ser consolidadas.
		</p>
	</div>
	
	<h:form>
	<c:set var="unidadeDocente"/>
	<table class="listagem" style="width: 80%;">
		<caption> Docentes </caption>
		<c:forEach var="docente" items="#{consolidacaoProducaoBean.docentes}" varStatus="loop">
		
			<c:if test="${unidadeDocente != docente.unidade.id}">
				<c:set var="unidadeDocente" value="${docente.unidade.id}"/>
				<tr>
					<td colspan="2" class="subFormulario">
						${docente.unidade.nome}
					</td> 
				</tr>
			</c:if>
		
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td width="95%"> 
					${docente.nome} <small> (Mat. ${docente.siape}) </small> 
				</td>
				<td> 
					<h:commandButton image="/img/seta.gif"
						action="#{consolidacaoProducaoBean.listarProducoes}"
						title="Listar produções">
						<f:setPropertyActionListener target="#{consolidacaoProducaoBean.docente.id}" value="#{docente.id}"/>
					</h:commandButton>
				</td>
			</tr>
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center; font-weight: bold;"> 
					Existem ${fn:length(consolidacaoProducaoBean.docentes)} docentes cujas validações de produções precisam ser consolidadas 
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>