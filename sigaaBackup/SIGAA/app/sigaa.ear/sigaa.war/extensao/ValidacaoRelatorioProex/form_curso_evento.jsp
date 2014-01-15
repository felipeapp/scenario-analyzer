<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
	width: 98%;
	overflow:auto;
}
</style>

<f:view>
	<h2><ufrn:subSistema /> &gt; Validação de Relatório </h2>

	<h:form id="formRelatorioProjetos">
		
		<table class="formulario" width="100%">
		<caption class="listagem">Validação de Relatório de Curso ou Evento de Extensão</caption>	
		
			<tr>
				<td colspan="2"><b> Código:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.codigo}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="2"><b> Título:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.titulo}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="2"><b> Período:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.dataInicio}" />
					até <h:outputText value="#{relatorioAcaoExtensao.obj.atividade.dataFim}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="2"><b> Coordenador(a):</b><br/>
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.coordenacao.pessoa.nome}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="2"><b> Tipo de Relatório:</b><br/>
					<h:outputText value="#{relatorioAcaoExtensao.obj.tipoRelatorio.descricao}" />
				</td>
			</tr>
	
			<tr>
				<td colspan="2"><b> Houve cobrança de taxa?</b><br />
					R$ <h:outputText value="#{relatorioAcaoExtensao.obj.taxaMatricula}">
							<f:convertNumber pattern="#,##0.00"/>
					</h:outputText>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Valor arrecadado:</b><br />
					R$ <h:outputText value="#{relatorioAcaoExtensao.obj.totalArrecadado}">
						<f:convertNumber pattern="#,##0.00"/>
					</h:outputText>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Público Estimado:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.publicoEstimado}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Público Real Atingido:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.publicoAtendido}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Total de concluintes:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.numeroConcluintes}" />
				</td>
			</tr>
					
			<tr>
				<td colspan="2"><b>Esta ação foi realizada:</b><br />
				<h:outputText value="#{relatorioAcaoExtensao.obj.acaoRealizada ? 'SIM' : (relatorioAcaoExtensao.obj.acaoRealizada == null ? '-' : 'NÃO')}" id="acaoFoiRealizada" /></td>	
			</tr>
		
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
			</tr>	

			<c:if test="${relatorioAcaoExtensao.obj.acaoRealizada != null && !relatorioAcaoExtensao.obj.acaoRealizada }">
				<tr>
					<td colspan="2" style="text-align: justify;"><b>Motivo da não realização desta ação:</b><br />
						<h:outputText value="#{relatorioAcaoExtensao.obj.motivoAcaoNaoRealizada}" id="motivoNaoRealizacao" />
					</td>	
				</tr>
			</c:if>
	
			<tr>
				<td colspan="2" align="justify"><b> Atividades Realizadas: </b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividadesRealizadas}" id="atividades" />
				</td>	
			</tr>
	
			<tr>
				<td colspan="2" align="justify"><b> Resultados Obtidos: Qualitativos.</b><br />
					<h:outputText  value="#{relatorioAcaoExtensao.obj.resultadosQualitativos}" id="qualitativos" />
				</td>	
			</tr>
	
			<tr>
				<td colspan="2" align="justify"><b> Resultados Obtidos: Quantitativos.</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.resultadosQuantitativos}" id="quantitativos" />
				</td>	
			</tr>
	
			<tr>
				<td colspan="2" align="justify"><b> Dificuldades Encontradas:</b><br />
					<h:outputText value="#{relatorioAcaoExtensao.obj.dificuldadesEncontradas}" id="dificuldades" />
				</td>	
			</tr>
	
			<tr>
				<td colspan="2" class="subFormulario"> Membros da Equipe </td>
			</tr>

			<c:if test="${not empty relatorioAcaoExtensao.obj.atividade.membrosEquipe}">
				<tr>
					<td colspan="2">
					<table width="100%" class="listagem" id="tbEquipe">
						<thead>
							<tr>
								<th>Nome</th>
								<th>Categoria</th>
								<th>Função</th>
								<th>Departamento</th>
								<th>Início</th>
								<th>Fim</th>
								<th style="text-align:right">Carga Horária</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="#{relatorioAcaoExtensao.obj.atividade.membrosEquipe}" var="membro" varStatus="count">
								<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td>${membro.pessoa.nome}</td>
									<td>${membro.categoriaMembro.descricao}</td>
									<td>
										<c:if test="${membro.funcaoMembro.id == COORDENADOR}"><font color='red'></c:if>
											${membro.funcaoMembro.descricao}
										<c:if test="${membro.funcaoMembro.id == COORDENADOR}"></font></c:if>
									</td>
									<td>
										<c:if test="${not empty membro.servidor}">${membro.servidor.unidade.sigla}</c:if>
									</td>
									<td><fmt:formatDate value="${membro.dataInicio}" pattern="dd/MM/yyyy"/> </td>
									<td><fmt:formatDate value="${membro.dataFim}" pattern="dd/MM/yyyy"/> </td>
									<td style="text-align:right">
										${membro.chCertificadoDeclaracao} Hs.
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					</td>
				</tr>
			</c:if>

			<tr>
				<td colspan="2" class="subFormulario">Lista de Arquivos</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>				
					<t:dataTable id="dataTableArq" value="#{relatorioAcaoExtensao.obj.arquivos}" var="anexo" 
						align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
						<t:column  width="97%">
							<h:outputText value="#{anexo.descricao}" />
						</t:column>
		
						<t:column>
							<h:commandButton image="/img/view.gif" action="#{relatorioAcaoExtensao.viewArquivo}"
								title="Ver Arquivo" alt="Ver Arquivo" onclick="$(idArquivo).value=#{anexo.idArquivo};" id="viewArquivo" />
						</t:column>	
					</t:dataTable>
				</td>
			</tr>
			
			<c:if test="${empty relatorioAcaoExtensao.obj.arquivos}">
				<tr><td colspan="6" align="center"><font color="red">Não há arquivos adicionados ao relatório</font> </td></tr>
			</c:if>
			
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento de utilização dos recursos financeiros </td>
			</tr>

			<tr>
				<td colspan="2">
				<table class="listagem">
						<tr>
							<td><c:if test="${not empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
								<table width="100%" class="listagem" id="dt">
								<thead>
									<tr>
										<th>Descrição</th>
										<th style="text-align: right;">FAEx (Interno)</th>
										<th style="text-align: right;">Funpec</th>
										<th style="text-align: right;">Outros (Externo)</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="#{relatorioAcaoExtensao.obj.detalhamentoRecursos}"
										var="consolidacao" varStatus="count">
										<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td>${consolidacao.elemento.descricao}</td>
											<td style="text-align: right;">R$ ${consolidacao.faex}</td>
											<td style="text-align: right;">R$ ${consolidacao.funpec}</td>
											<td style="text-align: right;">R$ ${consolidacao.outros}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							</c:if></td>
						</tr>

					<c:if test="${empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
						<tr>
							<td colspan="6" align="center">
								<font color="red">Não há itens de despesas cadastrados</font>
							</td>
						</tr>
					</c:if>

				</table>
				</td>
			</tr>

			<tr>
				<td colspan="2"><br/><br/></td>
			</tr>	

			<tr>
				<td colspan="2" class="subFormulario"> Validação do Relatório </td>
			</tr>	

			<tr>
				<th width="15%" class="required">Parecer:</th>
				<td>	
					<h:selectOneMenu id="autorizacaoProex" value="#{relatorioAcaoExtensao.obj.tipoParecerProex.id}" style="width: 30%;"
					onchange="if(this.value != '1') { $('justificativa').className='required' } else { $('justificativa').className='' }" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItem itemValue="1" itemLabel="APROVAR" />
						<f:selectItem itemValue="2" itemLabel="APROVAR COM RECOMENDAÇÃO" />						
						<f:selectItem itemValue="3" itemLabel="REPROVAR" />
						<f:selectItem itemValue="4" itemLabel="AÇÃO NÃO REALIZADA" />
					</h:selectOneMenu>
					<br/>
				</td>
			</tr>		
			<tr>			
				<th id="justificativa" valign="top">Justificativa: </th>
				<td>
					<h:inputTextarea  id="parecerDepartamento" value="#{relatorioAcaoExtensao.obj.parecerProex}" rows="5" style="width: 98%" />
				</td>
			</tr>
		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="confirmar" value="Confirmar Validação" action="#{relatorioAcaoExtensao.validarRelatorioProex}" /> 
						<h:commandButton id="cancelar" value="Cancelar" action="#{relatorioAcaoExtensao.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<script type="text/javascript">

	function setarRequiredJustificativa(){
		if ( $("autorizacaoProex").value != "1"){ 
			$("justificativa").className="required"
		} else {
			$("justificativa").className=""
		}
	}

	window.onload = setarRequiredJustificativa();
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>