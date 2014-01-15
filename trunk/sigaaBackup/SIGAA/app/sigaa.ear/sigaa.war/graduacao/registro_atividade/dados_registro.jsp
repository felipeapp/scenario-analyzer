<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript">
	function atualizarAno() {
		$('ativoEm').value = $F('form:ano');
	};
	// Verifica se a nota digitada é maior que 10. Se for, invalida.
	function verificaNotaMaiorDez(element) {
		verificaNotaMaior(element, 10.0);
	}
	//Verifica se a nota digitada é maior que 10. Se for, invalida.
	function verificaNotaMaior(element, maximo) {
		var valor = parseFloat(element.value.replace(',','.'));
		if (valor > maximo) {
			alert('A nota deve estar entre 0 e ' + maximo + '.');
			element.value = '';
		}
	}
</script>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> > ${registroAtividade.descricaoOperacao} &gt; Registro da Atividade </h2>

	<c:if test="${ registroAtividade.consolidacao && registroAtividade.migrado }">
		<div class="descricaoOperacao">
			<p><strong>Atenção!!</strong></p>
			<p>
				Esta atividade não contém todas as informações necessárias para sua consolidação.
				A atividade pode ter sido cadastrada originalmente no PontoA.
				Por favor, informe os dados solicitados para regularizar o cadastro.
			</p>
		</div>
	</c:if>

	<c:set var="discente" value="#{registroAtividade.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<input type="hidden" name="ativoEm" id="ativoEm" value="${registroAtividade.obj.ano}"/>
	
	<table class="formulario" style="width: 90%">
		<caption> Informe os Detalhes do Registro da Atividade </caption>
		<tbody>
		<tr>
			<th><b>Atividade:</b></th>
			<td> ${registroAtividade.obj.componente.descricao} </td>
		</tr>
		<tr>
			<th><b>Tipo da Atividade:</b></th>
			<td> ${registroAtividade.obj.componente.tipoAtividade.descricao} </td>
		</tr>

		<c:if test="${ registroAtividade.matricula || registroAtividade.validacao }">

			<tr>
				<th class="required"> Ano-Período: </th>
				<td>
					<t:inputText size="4" maxlength="4"  id="ano" value="#{registroAtividade.obj.ano}" onchange="atualizarAno();" onkeyup="return formatarInteiro(this);"/> . 
					<h:inputText size="1" maxlength="1" id="periodo" value="#{registroAtividade.obj.periodo}" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>

		</c:if>

		<c:if test="${ registroAtividade.consolidacao || registroAtividade.alteracaoGraduacao }">
			<tr>
				<th><b>Ano-Período:</b></th>
				<td> 
					${registroAtividade.obj.ano}.${registroAtividade.obj.periodo}
				</td>
			</tr>
		</c:if>

		<c:if test="${ registroAtividade.matricula 
			|| (registroAtividade.informarDocentesEnvolvidos && !registroAtividade.consolidacao) 
			|| (registroAtividade.consolidacao && registroAtividade.migrado)}">
			<c:if test="${(!registroAtividade.obj.componente.atividadeComplementar) 
			|| (registroAtividade.obj.componente.atividadeComplementar && registroAtividade.obj.componente.temOrientador)}">
				
				<c:if test="${registroAtividade.obj.componente.estagio || registroAtividade.obj.componente.atividadeColetiva}">
					<tr>
						<th class="required" style="font-weight:bold;"> CH Dedicada do(s) Orientador(es):</th>
						<td>
							<h:outputText value="#{registroAtividade.obj.detalhesComponente.chDedicadaDocente }h" />
						</td>
					</tr>
				</c:if>
			<tr>
				<th class="${ ( registroAtividade.obrigatorioInformarDocentesEnvolvidos ? 'required' : '' ) }"> Orientador:</th>
				<td>
					<c:if test="${ registroAtividade.permiteOrientadoresExternos }">
						<h:inputHidden value="#{registroAtividade.idOrientador}" id="idOrientador"/>
						<c:set var="idAjax" value="form:idOrientador"/>
						<c:set var="nomeAjax" value="orientador.nome"/>
						<c:set var="nomeDocente" value="${registroAtividade.nomeOrientador}" />
						<c:set var="cedidos" value="true" />
						<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
						
						<c:if test="${not empty registroAtividade.obj.registroAtividade && not empty registroAtividade.obj.registroAtividade.orientador &&
						empty registroAtividade.obj.registroAtividade.orientador.orientador}">
							<script type="text/javascript">
							buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteExterno_${contAjaxDocente}');
							</script>
						</c:if>
						
					</c:if>
					
					<%-- estagio só pode ter Servidor da instituição como orientador --%>
					<c:if test="${ !registroAtividade.permiteOrientadoresExternos }">
						<h:inputHidden id="idOri" value="#{registroAtividade.idOrientador}"></h:inputHidden>
						<h:inputText id="nomeOri" value="#{registroAtividade.nomeOrientador}" size="50" />
	
						<ajax:autocomplete
						source="form:nomeOri" target="form:idOri"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo,ativoEm={ativoEm}"
						parser="new ResponseXmlToHtmlListParser()"  />
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</c:if>
					
				</td>
			</tr>
			<tr>
				<th> Coorientador:</th>
				<td>
					<c:if test="${ registroAtividade.permiteOrientadoresExternos }">
						<h:inputHidden value="#{registroAtividade.idCoOrientador}" id="idCoOrientador"/>
						<c:set var="idAjax" value="form:idCoOrientador"/>
						<c:set var="nomeAjax" value="coorientador.nome"/>
						<c:set var="nomeDocente" value="${registroAtividade.nomeCoOrientador}" />
						<c:set var="cedidos" value="true" />
						<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
						
						<c:if test="${not empty registroAtividade.obj.registroAtividade && not empty registroAtividade.obj.registroAtividade.coOrientador &&
						empty registroAtividade.obj.registroAtividade.coOrientador.orientador}">
							<script type="text/javascript">
							buscarDocentePor_${contAjaxDocente}('buscaAjaxDocenteExterno_${contAjaxDocente}');
							</script>
						</c:if>
						
					</c:if>
					
					<%-- estagio só pode ter Servidor da instituição como orientador --%>
					<c:if test="${ !registroAtividade.permiteOrientadoresExternos }">
						<h:inputHidden id="idCoOri" value="#{registroAtividade.idCoOrientador}"></h:inputHidden>
						<h:inputText id="nomeCoOri" value="#{registroAtividade.nomeCoOrientador}" size="50" />
	
						<ajax:autocomplete
						source="form:nomeCoOri" target="form:idCoOri"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator2" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo,ativoEm={ativoEm}"
						parser="new ResponseXmlToHtmlListParser()"  />
						<span id="indicator2" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</c:if>
					
				</td>
			</tr>
			</c:if>
			<c:if test="${ registroAtividade.estagio }">
				<tr>
					<td colspan="2" class="subFormulario" style="text-align: center">
						Dados do Estágio
					</td>
				</tr>
				<tr>
					<th class="required"> Coordenador de Estágio: </th>
					<td>
					<%-- 
						<h:inputHidden value="#{registroAtividade.obj.registroAtividade.coordenador.id}" id="idCoordenador"/>
						<c:set var="idAjax" value="form:idCoordenador"/>
						<c:set var="nomeAjax" value="coordenador.nome"/>
						<c:set var="obrigatorio" value="${registroAtividade.obrigatorioInformarDocentesEnvolvidos}"/>
						<c:set var="nomeDocente" value="${registroAtividade.obj.registroAtividade.coordenador.nome}" />
						<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp"%>
					--%>
						<h:inputHidden id="idCoord" value="#{registroAtividade.obj.registroAtividade.coordenador.id}"></h:inputHidden>
						<h:inputText id="nomeCoord" value="#{registroAtividade.obj.registroAtividade.coordenador.pessoa.nome}" size="50" />
	
						<ajax:autocomplete
						source="form:nomeCoord" target="form:idCoord"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
				<tr>
					<th> Supervisor de Campo: </th>
					<td>
						<h:inputText value="#{registroAtividade.obj.registroAtividade.supervisor}" maxlength="100" style="width: 90%;"/>
					</td>
				</tr>
			</c:if>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao && not registroAtividade.migrado}">
			<tr>
				<th><b>Orientador:</b></th>
				<td>
					<c:forEach var="orientacao" items="#{registroAtividade.obj.registroAtividade.orientacoesAtividade}">
						${orientacao.orientador != null ? orientacao.orientador.pessoa.nome : orientacao.orientadorExterno.pessoa.nome} - ${orientacao.cargaHoraria}h<br/>
					 </c:forEach>
				</td>
			</tr>
			<c:if test="${ registroAtividade.estagio }">
				<tr>
					<th><b>Coordenador de Estágio:</b></th>
					<td> ${registroAtividade.obj.registroAtividade.coordenador.pessoa.nome}  </td>
				</tr>
				<tr>
					<th><b>Supervisor de Campo:</b></th>
					<td> ${registroAtividade.obj.registroAtividade.supervisor} </td>
				</tr>
			</c:if>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao || registroAtividade.validacao }">
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center;"> Resultado </td>
			</tr>

			<c:if test="${ registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal }">

				<c:choose>
					
					<c:when test="${registroAtividade.obj.metodoNota}">
						<tr>
							<th class="required"> Nota Final: </th>
							<td>
								<h:inputText value="#{registroAtividade.obj.mediaFinal}" size="4" maxlength="4" id="campoNota" 
									onkeydown="return(formataValor(this, event, 1))"  onblur="verificaNotaMaiorDez(this)">
									<f:converter converterId="convertNota"/>
								</h:inputText>
		
								<c:if test="${not registroAtividade.informarDocentesEnvolvidos}">
									<span style="font-variant: small-caps;">
										<h:selectBooleanCheckbox value="#{ registroAtividade.dispensa }" id="checkDispensa" onchange="marcarDispensa(this)"/>
										<label for="form:checkDispensa">Registrar como DISPENSADO</label>
									</span>
										<script>
											var marcarDispensa = function(dispensa) {
												if (dispensa.checked) {
													$('form:campoNota').disable();
												} else {
													$('form:campoNota').enable();
												}
											}
											marcarDispensa($('form:checkDispensa'));
										</script>
								</c:if>
							</td>
						</tr>
					</c:when>
					
					<c:when test="${registroAtividade.obj.metodoConceito}">
						<tr>
							<th> Conceito: </th>
							<td>
								<h:selectOneMenu value="#{registroAtividade.obj.conceito}" id="conceito">
									<f:selectItem itemLabel="--" itemValue=""/>
									<f:selectItems value="#{conceitoNota.orderedCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</c:when>	
					
				</c:choose>
				
			</c:if>

			<c:if test="${ !registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal }">
				<tr>
					<th> Situação: </th>
					<td style="padding: 8px;">
						<h:selectOneMenu style="width: 180px" value="#{registroAtividade.obj.situacaoMatricula.id}">
							<f:selectItems value="#{registroAtividade.resultados}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
		</c:if>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Selecionar Outra Atividade" immediate="true" action="#{registroAtividade.telaAtividades}" id="btnAtividades"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{registroAtividade.cancelar}" id="btnCancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{registroAtividade.verConfirmacao}" id="btnConfirmacao"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 
	<c:if test="${ registroAtividade.matricula || registroAtividade.validacao }">
		<script type="text/javascript">
			$('form:periodo').focus();
		</script>
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>