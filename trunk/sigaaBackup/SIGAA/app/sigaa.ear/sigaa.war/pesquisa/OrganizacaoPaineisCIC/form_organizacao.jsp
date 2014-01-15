<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Gerar Numeração dos Painéis de Resumos do CIC</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Gestor,</b></p>
		<p>Esta operação definirá uma numeração para cada trabalho submetido ao 
			Congresso de Iniciação Científica de acordo com a organização desejada, 
			permitindo identificar a localização de cada trabalho nos painéis.</p>
		<p>Apenas resumos com a situação <strong>APROVADO</strong> serão levados em conta na geração da numeração.</p>
	</div>
	<a4j:keepAlive beanName="organizacaoPaineis" />
	<h:form id="formDistribuicao">
		<table class="formulario" width="70%">
		<caption>Dados para gerar a numeração</caption>
			<tr>
				<th>Congresso:</th>
				<td>
					<h:outputText id="congresso" style="font-weight: bold;" value="#{organizacaoPaineis.obj.congresso.descricao}" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Número de painéis:</th>
				<td>
					<h:inputText id="numeroPaineis" value="#{organizacaoPaineis.obj.numeroPaineis}" 
						size="3" maxlength="3" onkeyup="formatarInteiro(this);"/>
					<ufrn:help>Quantidade de painéis disponíveis no espaço físico para comportar a apresentação dos trabalhos de um mesmo dia.</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Organização da apresentação dos trabalhos dos centros/unidades</td>
			</tr>
			<tr>
				<th class="required">Centro/Unidade:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{organizacaoPaineis.diaApresentacaoCentro.centro.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{siglaUnidadePesquisaMBean.unidadesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Dia da Apresentação:</th>
				<td>
					<h:selectOneMenu id="diaApresentacao" value="#{organizacaoPaineis.diaApresentacaoCentro.dia}">
						<f:selectItem itemLabel="1º Dia" itemValue="1"/>
						<f:selectItem itemLabel="2º Dia" itemValue="2"/>
						<f:selectItem itemLabel="3º Dia" itemValue="3"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<h:commandButton action="#{organizacaoPaineis.adicionarDiaApresentacaoCentro}" value="Adicionar"/>
				</td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<table class="listagem">
						<c:set var="dia_" value="0"/>
						<c:set var="total_" value="0"/>
						<c:forEach items="#{organizacaoPaineis.obj.diasApresentacao}" var="diaApresentacao" varStatus="row">
							<c:if test="${dia_ != diaApresentacao.dia}">
								<c:if test="${dia_ != 0}">
									<tr style="font-weight: bold; border-top: 1px solid">
										<td>Total:</td>
										<td>${ total_ } trabalhos</td>
										<c:set var="total_" value="0"/>
									</tr>
								</c:if>	
								<c:set var="dia_" value="${diaApresentacao.dia}"/>
								<tr>
									<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">${ diaApresentacao.descricaoDia }</td>
								</tr>
								
							</c:if>
							<tr>
								<td>${ diaApresentacao.centro.nome }</td>
								<td>${ diaApresentacao.numeroTrabalhos } trabalhos</td>
								<c:set var="total_" value="${total_ + diaApresentacao.numeroTrabalhos}"/>
							</tr>
							<c:if test="${row.index+1 == fn:length(organizacaoPaineis.obj.diasApresentacao)}">
								<tr style="font-weight: bold; border-top: 1px solid">
									<td>Total:</td>
									<td>${ total_ } trabalhos</td>
								</tr>
							</c:if>	
						</c:forEach>
					</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnGerarNumeracao" action="#{organizacaoPaineis.gerarNumeracaoResumos}" value="Gerar Numeração"/>
						<h:commandButton id="btnCancelar" action="#{organizacaoPaineis.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>