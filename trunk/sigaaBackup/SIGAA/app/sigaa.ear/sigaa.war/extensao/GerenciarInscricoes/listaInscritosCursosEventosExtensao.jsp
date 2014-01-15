<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante"%>


<c:set var="STATUS_INSCRITO" value="<%= StatusInscricaoParticipante.INSCRITO %>" scope="request" />
<c:set var="STATUS_APROVADO" value="<%= StatusInscricaoParticipante.APROVADO %>" scope="request" />
<c:set var="STATUS_RECUSADO" value="<%= StatusInscricaoParticipante.RECUSADO %>" scope="request" />
<c:set var="STATUS_CANCELADO" value="<%= StatusInscricaoParticipante.CANCELADO %>" scope="request" />

<f:view>

	<h2><ufrn:subSistema /> &gt; Gerenciar Inscritos</h2>
	
	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	<a4j:keepAlive beanName="gerenciarInscritosCursosEEventosExtensaoMBean" />
	
	<h:form id="formListaInscritosCursosEventos">
	
	<div class="descricaoOperacao">
		<p> Caro(a) Usuário(a), </p>
		<p> Abaixo estão listados todos os participantes que realizaram inscrições na atividade ou mini atividade de extensão selecionada.</p>
		<p> A partir dessa listagem é possível aprovar os participantes, confirmar o pagamento da inscrição, 
		     visualizar os questionários respondidos pelos usuários, entre outras operações.</p>
		<br/>
		<p> <strong>IMPORTANTE:</strong> Para as inscrições aprovadas utilize a operação 
		<h:commandLink value="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" />
		para informar a frequência, emitir declarações, certificados, entre outras. As operações contidas nessa página são referentes apenas às inscrições.
		</p>
		<br/>
		<p>
		<strong>Status das inscrições:</strong>
		</p>
		
		<ul>
			<li> <strong>Inscritas:</strong> Indica os participantes inscritos que necessitam de aprovação. Seja porque a inscrição exigia a aprovação pelo coordenador ou a inscrição está esperando a confirmação do pagamento.</li>
			<li> <strong>Aprovadas:</strong> Indica os participantes que irão participar da ação, vão poder receber certificados etc.</li>
			<li> <strong>Recusadas:</strong> Indica que o coordenador do curso/evento negou a inscrição do participante.</li>
			<li> <strong>Canceladas:</strong> O participante cancelou a sua participação no curso ou evento.</li>
		</ul>
		
		<p> São mostradas as seguintes informações com relação ao pagamento:</p>
		<ul>
			<li>NÃO GERENCIADO: O pagamento da atividade não é gerenciado pelo sistema. Não é possível determinar se o usuário realizou o pagamento ou não.</li>
			<li>EM ABERTO: A confirmação do pagamento da atividade ainda não foi realizada no sistema.</li>
			<li>CONFIRMADO: O pagamento da GRU foi confirmado no sistema.</li>
			<%-- <li>ESTORNADO: O pagamento da GRU foi estornado no sistema.</li> --%>
		</ul>
		
	</div>
	
		<table class="formulario" style="width: 70%; margin-bottom: 20px;">
			<caption> Filtro da Busca </caption>
			<tr>
				<th> Status da Inscrição: </th>
				<td>
					<h:selectOneMenu id="comboBoxBibliotecasDosFasciculos" value="#{gerenciarInscritosCursosEEventosExtensaoMBean.idStatusInscricaoParticipante}">
						<f:selectItems value="#{gerenciarInscritosCursosEEventosExtensaoMBean.statusInscricaoComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th> Ordenação: </th>
				<td>
					<h:selectOneMenu value="#{gerenciarInscritosCursosEEventosExtensaoMBean.valorCampoOrdenacao}">
						<f:selectItems value="#{gerenciarInscritosCursosEEventosExtensaoMBean.campoOrdenacaoComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cmdFiltrarInscritos" value=" Filtrar " action="#{gerenciarInscritosCursosEEventosExtensaoMBean.buscarParticipantesInscritos}"  />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
		<c:if test="${not empty gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada}">
			<table class="formulario" style="width: 80%; margin-bottom: 20px;">
			  	<caption>Dados da Ação de Extensão</caption>
				<tr>			
					<th><b>Código:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.codigo}</td>
				</tr>
				<tr>
					<th><b>Título:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.titulo}</td>
				</tr>
				
				<tr>
					<th><b>Ano:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.ano}</td>
				</tr>
				
				<tr>
					<th><b>Coordenação:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.projeto.coordenador.pessoa.nome}</td>
				</tr>
				<tr>
					<th><b>Período:</b></th>
					<td>
						<fmt:formatDate value="${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.dataInicio}" pattern="dd/MM/yyyy"/>										 
						até 
						<fmt:formatDate value="${gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.dataFim}" pattern="dd/MM/yyyy"/>
						
					</td>
				</tr>
			</table>
		</c:if>
	
		<c:if test="${not empty gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada}">
			<table class="formulario" style="width: 80%; margin-bottom: 20px;">
			  	<caption>Dados da mini Ação de Extensão</caption>
				<tr>			
					<th><b>Atividade:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.atividade.codigo} - ${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.atividade.titulo}</td>
				</tr>
				<tr>
					<th><b>Título:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.titulo}</td>
				</tr>
				
				<tr>
					<th><b>Coordenação:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.atividade.projeto.coordenador.pessoa.nome}</td>
				</tr>
				<tr>
					<th><b>Período:</b></th>
					<td>
						<fmt:formatDate value="${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.inicio}" pattern="dd/MM/yyyy"/>										 
						até 
						<fmt:formatDate value="${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.fim}" pattern="dd/MM/yyyy"/>
					</td>
				</tr>
				<tr>
					<th><b>Local:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.local}</td>
				</tr>
				<tr>
					<th><b>Horário:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.horario}</td>
				</tr>
				<tr>
					<th><b>Tipo:</b></th>
					<td>${gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.tipoSubAtividadeExtensao.descricao}</td>
				</tr>
				
			</table>
		</c:if>
	
	
		<div class="infoAltRem">
		
			<%-- Comentado por enquanto já que no sistema anterior de extensão não existiga essa possibilidade.
			<h:graphicImage value="/img/extensao/businessman_preferences.png" />
			
			<c:if test="${not empty gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada}"> 
				<h:commandLink value="Realizar Nova Inscrição" action="#{realizaInscricaoParticipanteCoordenadorMBean.inscreverParticipanteAtividade}">
					<f:param name="idAtividadeSelecionada" value="#{gerenciarInscritosCursosEEventosExtensaoMBean.atividadeSelecionada.id}" />
				</h:commandLink>
			</c:if>
			
			
			<c:if test="${not empty gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada}"> 
				<h:commandLink value="Realizar Nova Inscrição" action="#{realizaInscricaoParticipanteCoordenadorMBean.inscreverParticipanteSubAtividade}">
					<f:param name="idSubAtividadeSelecionada" value="#{gerenciarInscritosCursosEEventosExtensaoMBean.subAtividadeSelecionada.id}" />
				</h:commandLink>
			</c:if> --%>
	         
	        <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;" id="labViewInscricao"/> Visualizar Dados Participante
	        
	        <h:graphicImage value="/img/imprimir.gif" style="overflow: visible;" id="labImprimirGRUArquivo" width="16" height="16" />: Imprimir GRU
	        
	        <br/>
	        
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="labVisualizarArquivo"/>: Visualizar Arquivo
	        
	        <h:graphicImage value="/img/questionario.png" style="overflow: visible;" id="labViewQuestionario"/> Visualizar Questionário Inscrição
	        
	    </div>
	
		<table class="listagem" style="width: 100%;">
			<caption> Inscrições Realizadas </caption>
			
			<thead>
				<tr>
					<th style="text-align:center" width="8%">
						<a id="linkMarcar" href="javascript:void(0)" onclick="marcarTodosCheckboxes(0);">TODOS</a>
					</th>
					<th width="1%">Nº</th>		
					<th style="width: 10%;">CPF</th>
					<th style="width: 9%;">Passaporte</th>
					<th style="width: 25%;">Nome</th>
					<th style="width: 25%;">Instituição</th>
					<th style="width: 7%;">Status do Pagamento</th>
					<th style="width: 1%;"> </th>
					<th style="width: 1%;"> </th>
					<th style="width: 1%;"> </th>
					<th style="width: 1%;"> </th>
				</tr>
			</thead>
			
			<c:if test="${gerenciarInscritosCursosEEventosExtensaoMBean.qtdInscricoes == 0}">
				<tr>
					<td colspan="11" style="font-weight: bold; text-align: center; color: red;">Não existem inscrições realizadas para essa atividade </td>
				</tr>
			</c:if>
			<c:if test="${gerenciarInscritosCursosEEventosExtensaoMBean.qtdInscricoes > 0}">
				
				<c:set var="idStatusInscricao" value="-1" scope="request" />
				
				<c:forEach var="inscricao" items="#{gerenciarInscritosCursosEEventosExtensaoMBean.inscricoes}" varStatus="status">
					
					<c:if test="${ idStatusInscricao != inscricao.statusInscricao.id}">
						<c:set var="idStatusInscricao" value="${inscricao.statusInscricao.id}" scope="request"/>
						<tr>
							<td colspan="11" class="subFormulario"> 
								<a id="marcar2" href="javascript:void(0)" onclick="marcarTodosCheckboxes(${inscricao.statusInscricao.id});"> ${inscricao.statusInscricao.descricao}S </a>
								( ${inscricao.statusInscricao.statusInscrito  ?  gerenciarInscritosCursosEEventosExtensaoMBean.quantidadeParticipanteInscritos : ''} 
								  ${inscricao.statusInscricao.statusAprovado  ?  gerenciarInscritosCursosEEventosExtensaoMBean.quantidadeParticipanteAprovados : ''} 
							 	  ${inscricao.statusInscricao.statusCancelado ?  gerenciarInscritosCursosEEventosExtensaoMBean.quantidadeParticipanteCancelados : ''} 
							 	  ${inscricao.statusInscricao.statusRecusado  ?  gerenciarInscritosCursosEEventosExtensaoMBean.quantidadeParticipanteRecusados : ''}   )
							</td>
						</tr>
					</c:if>
					
					<tr>
						<td style="text-align: center;">
							<h:selectBooleanCheckbox id="check_${inscricao.statusInscricao.id}_${inscricao.id}" value="#{inscricao.marcado}"/>
						</td>
						<td>${status.count}</td>		
						<td>${inscricao.cadastroParticipante.cpf}</td>
						<td>${inscricao.cadastroParticipante.passaporte}</td>
						<td>${inscricao.cadastroParticipante.nome}</td>
						<td>${inscricao.instituicao}</td>
						<td style="text-align: center;">${inscricao.statusPagamento == null ? '- - -' : inscricao.statusPagamento.descricao}</td>
						
						<td>
							<h:commandLink id="viewInformacoesParticipante" title="Visualizar Dados Participante"
								action="#{gerenciarInscritosCursosEEventosExtensaoMBean.visualizarDadosParticipante}">
								<f:param name="idCadastroParticipante" value="#{inscricao.cadastroParticipante.id}" />
								<h:graphicImage url="/img/monitoria/businessman_view.png" />
							</h:commandLink>
						</td>
						
						<td>
							<h:commandLink title="Emitir GRU" id="cmdLinkEmitirGRUPagamento"
									action="#{gerenciarInscritosCursosEEventosExtensaoMBean.emitirGRUPagamentoInscricao}" 
									rendered="#{inscricao.statusInscricao.statusInscrito}">
								<f:param name="idInscricaoSelecionada" value="#{inscricao.id}" />
								<h:graphicImage url="/img/imprimir.gif" width="16" height="16" />
							</h:commandLink>
						</td>
						
						<td>
							<h:commandLink title="Visualizar arquivo: #{ inscricao.descricaoArquivo }" id="visualizarArquivoInscricao" target="_blank"
									action="#{gerenciarInscritosCursosEEventosExtensaoMBean.viewArquivoInscricao}" 
									rendered="#{inscricao.idArquivo > 0}">
								<f:param name="idArquivo" value="#{inscricao.idArquivo }" />
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionario" 
									rendered="#{inscricao.questionarioRespostas != null}"
									action="#{gerenciarInscritosCursosEEventosExtensaoMBean.viewRespostaQuestionarioInscricao}">
									<f:param name="idQuestionario" value="#{inscricao.questionarioRespostas.id}" />
								<h:graphicImage url="/img/questionario.png"/>
							</h:commandLink>
						</td>
						
						
					</tr>
				</c:forEach>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="11" style="text-align: center;">
						
						<h:commandButton id="cmdAceitarRecusarInscritos" value="Aprovar Participantes" 
							action="#{gerenciarInscritosCursosEEventosExtensaoMBean.aprovarParticipantesInscritos}" onclick="return confirm('Confirma a aprovação das inscrições? ');" />
						
						<h:commandButton id="cmdRecusarInscritosSelecionados" value="Recusar Participantes" 
							action="#{gerenciarInscritosCursosEEventosExtensaoMBean.recursarParticipantesSelecionados}" onclick="return confirm('Confirma o cancelamento das inscrições?');" />
						
						<h:commandButton id="cmdConfirmarPagamentoInscricao" value="Confirmar Pagamento" 
							action="#{gerenciarInscritosCursosEEventosExtensaoMBean.preConfirmarPagamentoManualmente}"  />
						
						<%-- COMENTADO POR ENQUANTO, PORQUE NINGUÉM SABE SE TEM COMO DEVOLVER O DINHEIRO PAGO NA GRU DA CONTA DA UFRN
						     SE ALGUM DIA O DINHEIRO PODER DE ALGUMA FORMA SER DEVOLVIDO, DESCOMENTAR ISSO !!!! Ass.: Jadson José dos Santos 15/02/2013   
						<h:commandButton id="cmdButtonExtornarPagamento"  value="Estornar Pagamentos"
							action="#{gerenciarInscritosCursosEEventosExtensaoMBean.preEstornarPagamento}">		
						</h:commandButton>
						--%>
						
						<h:commandButton id="cancelar" value="Cancelar" 
							action="#{gerenciarInscricoesCursosEventosExtensaoMBean.telaListaCursosEventosParaInscricao}" immediate="true"  />	
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	</h:form>
	
</f:view>	


<script type="text/javascript">

var mark = false;
var markInscritos = false;
var markAprovados = false;
var markRecusados = false;
var markCancelados = false;

function marcarTodosCheckboxes(statusInscricao){
	var checkboxes = document.getElementsByTagName('INPUT');
	
	switch(statusInscricao){
		case 0:
			mark = mark ? false : true;
			markInscritos = markAprovados = markRecusados = markCancelados = mark; 
			$('linkMarcar').innerHTML = mark ? 'NENHUM' : 'TODOS';
			for (i in checkboxes) {
				var input = checkboxes[i];
				if(input.type == 'checkbox')
					input.checked = mark;
			}
			break;
	
		case 1:
			markInscritos = markInscritos ? false : true;
			for (i in checkboxes) {
				var input = checkboxes[i];
				if(input.type == 'checkbox' && input.id.indexOf("check_"+${STATUS_INSCRITO}) != -1 )
					input.checked = markInscritos;
			}
			break;
		case 3:
			markAprovados = markAprovados ? false : true;
			for (i in checkboxes) {
				var input = checkboxes[i];
				if(input.type == 'checkbox' && input.id.indexOf("check_"+${STATUS_APROVADO}) != -1 )
					input.checked = markAprovados;
			}
			break;
		case 4:
			markRecusados = markRecusados ? false : true;
			for (i in checkboxes) {
				var input = checkboxes[i];
				if(input.type == 'checkbox' && input.id.indexOf("check_"+${STATUS_RECUSADO}) != -1 )
					input.checked = markRecusados;
			}
			break;
		case 5:
			markCancelados = markCancelados ? false : true;
			for (i in checkboxes) {
				var input = checkboxes[i];
				if(input.type == 'checkbox' && input.id.indexOf("check_"+${STATUS_CANCELADO}) != -1)
					input.checked = markCancelados;
			}
			break;
	}
}

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>