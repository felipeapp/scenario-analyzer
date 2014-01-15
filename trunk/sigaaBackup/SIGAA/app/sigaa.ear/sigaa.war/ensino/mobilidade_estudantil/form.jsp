<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Mobilidade Estudantil</h2>
<a4j:keepAlive beanName="mobilidadeEstudantil" />
	<div class="descricaoOperacao">
		<p>
			<b> Caro Coordenador, </b>
		</p> 	
		<p>Através deste formulário será possível realizar o cadastro do Mobilidade Estudantil</p>
		<p><b>Art.279.</b> É permitido ao aluno de graduação cursar componentes curriculares em outra 
           instituição de ensino superior, legalmente reconhecida, fora da área de atuação da instituição de ensino.		
		</p>
	</div>
	<h:form id="form">
		<table class="formulario" style="width: 90%">
			<caption> Cadastro de Mobilidade Estudantil</caption>
			<tr>
				<td colspan="2" class="subFormulario"> Dados do Aluno</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="visualizacao" style="width: 100%;">
						<tr>
							<th style="width: 40%;">
								Nome:
							</th>
							<td>
							    ${mobilidadeEstudantil.discente.matricula} - ${mobilidadeEstudantil.discente.pessoa.nome}
							</td>		
						</tr>
						<tr>
							<th>
								Curso:
							</th>
							<td>
								<c:choose>
									<c:when test="${not empty mobilidadeEstudantil.discente.curso}">
										${mobilidadeEstudantil.discente.curso.descricao}										
									</c:when>
									<c:otherwise>
										${mobilidadeEstudantil.discente.matrizCurricular.curso.descricao} <br />				
									</c:otherwise>
								</c:choose>								
							</td>		
						</tr>
						<tr>
							<th>Departamento:</th>
							<td>
								${mobilidadeEstudantil.discente.unidade.nome}
							</td>
						</tr>	
						<tr>
							<th> Status: </th>
							<td> ${mobilidadeEstudantil.discente.statusString } </td>
						</tr>						
						<tr>
							<th>Tipo:</th>
							<td> ${mobilidadeEstudantil.discente.tipoString } </td>						
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Dados da Mobilidade</td>
			</tr>	
			<tr>
				<th style="width: 30%;" class="obrigatorio">Tipo da Mobilidade:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="tipo" value="#{mobilidadeEstudantil.obj.tipo}">
							<f:selectItem itemValue="0" itemLabel="-- Selecione o Tipo da Mobilidade --"/>
							<f:selectItems value="#{mobilidadeEstudantil.allTiposCombo}"/>
							<a4j:support event="onchange" reRender="subtipo" oncomplete="mostraCampos(this);"/>
						</h:selectOneMenu>
			            <a4j:status>
			                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>				
					</a4j:region>								
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Sub-Tipo da Mobilidade:</th>
				<td>
					<h:selectOneMenu id="subtipo" value="#{mobilidadeEstudantil.obj.subtipo}" onchange="mostraPais(this);">
						<f:selectItem itemValue="0" itemLabel="-- Selecione o Sub-Tipo da Mobilidade --"/>
						<f:selectItems value="#{mobilidadeEstudantil.allSubTiposCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr id="mobInterna">
				<th class="obrigatorio">Campus de Destino:</th>
				<td>
					<h:selectOneMenu id="campus" value="#{mobilidadeEstudantil.obj.campusDestino.id}">
						<f:selectItem itemValue="0" itemLabel="-- Selecione o Campus --"/>
						<f:selectItems value="#{campusIes.allCampusCombo}"/>
					</h:selectOneMenu>						
				</td>				
			</tr>		
			<tr id="mobIesExterna">
				<th class="obrigatorio">Instituição de Ensino:</th>
				<td>
					<h:inputText id="iesExterna" value="#{mobilidadeEstudantil.obj.iesExterna}" size="60" maxlength="100"/>
				</td>				
			</tr>
			<tr id="modPais">
				<th class="obrigatorio">País:</th>
				<td>
					<h:selectOneMenu id="pais" value="#{mobilidadeEstudantil.obj.paisExterna.id}" >
						<f:selectItem itemValue="0" itemLabel="-- Selecione o País --"/>
						<f:selectItems value="#{pais.allCombo}"/>
					</h:selectOneMenu>					
				</td>				
			</tr>		
			<tr id="modCidade">
				<th class="obrigatorio">Cidade:</th>
				<td>
					<h:inputText id="cidade" value="#{mobilidadeEstudantil.obj.cidade}" size="60" maxlength="100"/>					
				</td>				
			</tr>								
			<tr>
				<th class="obrigatorio">Ano-Período Início:</th>
				<td>
					<h:inputText id="ano" value="#{mobilidadeEstudantil.obj.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" />-
					<h:inputText id="periodo" value="#{mobilidadeEstudantil.obj.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" /><ufrn:help>Período Regular (1 ou 2)</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Número de Períodos:</th>
				<td>
					<h:inputText id="numeroPeriodos" value="#{mobilidadeEstudantil.obj.numeroPeriodos}" size="2" maxlength="2" onkeyup="return formatarInteiro(this);"/>
					<ufrn:help>Quantidade de Períodos que o aluno ficará afastado, contando do Ano-Período Inicial.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Observação:</th>
				<td>
					<h:inputTextarea cols="80" rows="3" id="observacao" value="#{ mobilidadeEstudantil.obj.observacao }"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request" />
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp" %>	
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btcadastrar" value="Confirmar" action="#{mobilidadeEstudantil.cadastrar}"/>						
						<h:commandButton id="btSelecionaDiscente" value="<< Selecionar Outro Discente" action="#{mobilidadeEstudantil.iniciar}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{mobilidadeEstudantil.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>			
		</table>
	
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
	
	<c:if test="${not empty mobilidadeEstudantil.historicoMovimentacoes}">
		<%@include file="historico.jsp"%>
	</c:if>	
</f:view>

<script type="text/javascript">
	function mostraCampos(obj) {	
		if (obj.value == '0'){
			$('mobInterna').hide();
			$('mobIesExterna').hide();
		}
		if (obj.value == '1'){
			$('mobInterna').show();			
			$('mobIesExterna').hide();
		}
		if (obj.value == '2'){
			$('mobInterna').hide();			
			$('mobIesExterna').show();
		}	
		mostraPais(document.getElementById("form:subtipo"));											
	}	

	function mostraPais(obj) {	
		if (document.getElementById("form:tipo").value == '2' && obj.value == '2'){			
			$('modPais').show();
			$('modCidade').show();
		}else{
			$('modPais').hide();
			$('modCidade').hide();
		}
	}		
	mostraCampos(document.getElementById("form:tipo"));
	mostraPais(document.getElementById("form:subtipo"));
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>