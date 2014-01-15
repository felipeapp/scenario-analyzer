<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<f:view>
	<h2><ufrn:subSistema/> &gt; Extrapolar Cr�ditos</h2>

		<table class="visualizacao" width="80%">
			<tr>
				<th>Nome:</th>
				<td>${ extrapolarCredito.obj.discente.nome }</td>
			</tr>
			<tr>
				<th> Ano/Per�odo de Ingresso: </th>
				<td> ${extrapolarCredito.obj.discente.anoPeriodoIngresso} </td>
			</tr>
			<tr>
				<th> Forma de Ingresso: </th>
				<td> ${extrapolarCredito.obj.discente.formaIngresso.descricao} </td>
			</tr>
			<tr>
				<th width="25%"> Matriz Curricular: </th>
				<td> ${extrapolarCredito.obj.discente.matrizCurricular} </td>
			</tr>
			<tr>
				<th> Cr�ditos M�ximo do Curriculo: </th>
				<td> ${extrapolarCredito.obj.discente.curriculo.crMaximoSemestre} </td>
			</tr>			
			<tr>
				<th> Cr�ditos M�nimo do Curriculo: </th>
				<td> ${extrapolarCredito.obj.discente.curriculo.crMinimoSemestre} </td>
			</tr>						
		</table>
		
		<br />
		
		<h:form id="form">
			<table class="formulario" width="60%">
				<caption>Extrapolar Cr�ditos M�nimo ou M�ximo</caption>
				<tbody>
					<tr>
						<th class="required" width="45%">Ano-Periodo:</th>
						<td>
							<h:inputText value="#{extrapolarCredito.obj.ano}" size="4" maxlength="4" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="ano"/> - <h:inputText value="#{extrapolarCredito.obj.periodo}" size="1" maxlength="1" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="periodo"/>
						</td>
					</tr>
					<tr>
						<th class="required">M�ximo de Cr�ditos Permitidos:</th>
						<td>
							<h:inputText value="#{extrapolarCredito.obj.crMaximoExtrapolado}" size="4" maxlength="3" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="crMax"/>
						</td>
					</tr>
					<tr>
						<th class="required">M�nimo de Cr�ditos Permitidos:</th>
						<td>
							<h:inputText value="#{extrapolarCredito.obj.crMinimoExtrapolado}" size="4" maxlength="3" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="crMin"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<c:set var="exibirApenasSenha" value="true" scope="request"/>
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</td>
					</tr>				
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Cadastrar" action="#{extrapolarCredito.cadastrar}" id="cadastro"/>
							<h:commandButton value="<< Voltar" action="#{extrapolarCredito.telaBuscaDiscente}" id="voltar"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{extrapolarCredito.cancelar}" id="cancelarOp"/>
						</td>
					</tr>
				</tfoot>
			</table>
			<br/>
			<c:if test="${not empty extrapolarCredito.permissoes}">
				<table class="formulario" width="100%">
					<tr>
						<td colspan="2">
							<div class="infoAltRem">
								<h:graphicImage value="/img/delete.gif"style="overflow: visible;" id="legenda"/>: Excluir Permiss�o de Extrapolar
							</div>
						</td>
					</tr>				
					<tr>
						<td colspan="2">
							<table class="subFormulario" width="100%">
								<caption>Hist�rico de Permiss�es</caption>
								<thead>
									<td style="text-align: center;">Ano-Periodo</td>
									<td style="text-align: right;">Cr M�ximo</td>
									<td style="text-align: right;">Cr M�nimo</td>
									<td style="text-align: center;">Cadastrado</td>
									<td width="3%"></td>
								</thead>
								
								<tbody>
									<c:forEach items="#{extrapolarCredito.permissoes}" var="permissao" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td style="text-align: center;">${permissao.ano}-${permissao.periodo}</td>
											<c:choose>
												<c:when test="${not empty permissao.crMaximoExtrapolado}">									
													<td style="text-align: right;">${permissao.crMaximoExtrapolado}</td>
													<td style="text-align: right;">${permissao.crMinimoExtrapolado}</td>
												</c:when>
												<c:otherwise>
													<td style="text-align: left;" colspan="2">${(permissao.extrapolarMaximo?'Extrapolar M�ximo':'Extrapolar M�nimo')} (valor n�o definido)</td>
												</c:otherwise>
											</c:choose>
											<td style="text-align: center;"><fmt:formatDate value="${permissao.dataCadastro}" pattern="dd/MM/yyyy '�s' HH:mm:ss" /></td>
											<td>
												<h:commandLink id="excluirPermissao" title="Excluir Permiss�o de Extrapolar" action="#{extrapolarCredito.excluir}" onclick="if (!confirm('Se o aluno ja fez a matricula, a exclus�o desta permiss�o n�o vai desfazer a matricula do aluno. \nTem certeza que deseja excluir essa permiss�o?')) return false">
													<h:graphicImage url="/img/delete.gif"/>
													<f:param name="id" value="#{permissao.id}"/>
												</h:commandLink>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</table>
			</c:if>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>