<%@include file="/public/include/cabecalho.jsp"%>

<style>
.periodo {
	font-weight: bold;
	color: #292;
}

.fechado {
	color: #555;
}

.nivel {
	text-transform: uppercase;
}


table.listagem tr td.agrupador{
	height:10px;
	font-weight: bold;
	background-color: #EDF1F8;
	text-transform: uppercase;
	border-top: 1px solid #DAE5F8;
}
.colData{
	text-align: center !important;
}
.colVagas{
	text-align: right !important;
}
</style>

<f:view>
	<h:outputText value="#{processoSeletivo.create}" />
	<h:outputText value="#{inscricaoSelecao.create}" />

	<h2>Processos Seletivos - ${processoSeletivo.nivelDescricao}</h2>

	<h:form id="form" prependId="false">

		<div class="descricaoOperacao">
		<p><b>Caro visitante,</b></p>
		<p>Nesta p�gina voc� encontrar� os �ltimos processos seletivos cadastrados
		no SIGAA para que voc� possa consultar e, caso o per�odo de inscri��es esteja aberto, preencher o formul�rio
		destinado para tal .</p>
		<p>Ser� poss�vel visualizar as informa��es destes processos, como
		o curso a que ele se refere, o per�odo de inscri��o, alguns arquivos
		associados (como editais e manuais) e as instru��es aos candidatos.</p>
		<p>Para cada processo listado est� tamb�m dispon�vel um <b><i>
		formul�rio de inscri��o</i></b> para os candidatos.</p>
		<p>Os per�odos dos processos seletivos marcados na cor <b>verde</b> est�o em aberto.</p>
		</div>

		<table class="formulario" id="consulta" width="50%">
			<caption>Consultar inscri��es</caption>
			<tbody>
				
				
				<tr>
					<th width="30%">
							<h:outputText value="CPF:" id="labCPF" 
								rendered="#{!inscricaoSelecao.obj.pessoaInscricao.estrangeiro}"/>
							<h:outputText value="Passaporte:" id="labPassaporte" 
								rendered="#{inscricaoSelecao.obj.pessoaInscricao.estrangeiro}"/>
					</th>
					<td>
							<h:inputText
								value="#{inscricaoSelecao.obj.pessoaInscricao.cpf}" size="15" maxlength="14"
								onkeypress="return formataCPF(this, event, null)" id="txtCPF"
								 rendered="#{!inscricaoSelecao.obj.pessoaInscricao.estrangeiro}" >
								<f:converter converterId="convertCpf" />
							</h:inputText>
							
							<h:inputText
								value="#{inscricaoSelecao.obj.pessoaInscricao.passaporte}" size="15" maxlength="14"
									 id="txtPassaporte"
									 rendered="#{inscricaoSelecao.obj.pessoaInscricao.estrangeiro}" >
							</h:inputText>
					</td>
				</tr>
				<tr>
					<th>
					</th>
					<td> 
						<h:selectBooleanCheckbox  value="#{inscricaoSelecao.obj.pessoaInscricao.estrangeiro}" 
							id="txtEstrangeiro" onclick="submit()" immediate="true">
						</h:selectBooleanCheckbox>	
						A pessoa � estrangeira e n�o possui CPF.
					</td>
				</tr>
				
			</tbody>
			<tfoot>
				<tr>	
					<td colspan="2">
						<h:inputHidden value="#{processoSeletivo.nivel}" />
						<h:commandButton value="Buscar" actionListener="#{inscricaoSelecao.buscarInscricoes}">
							<f:attribute name="nivel" value="#{processoSeletivo.nivel}" />
						</h:commandButton>&nbsp;
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{inscricaoSelecao.cancelar}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>

		<c:set var="processosSeletivos" value="#{processoSeletivo.allVisiveis}" />

	<br/>
	<center>
		<h:messages />
		<div class="infoAltRem">
			<c:if test="${processoSeletivo.processoStricto}">
			<f:verbatim><h:graphicImage url="#{ctx}/public/images/link.png" width="15px" height="15px" />: </f:verbatim>Acessar p�gina do programa
			&nbsp;
			</c:if>
			<f:verbatim><h:graphicImage value="#{ctx}/img/seta.gif" style="overflow: visible;" alt="*"/>: </f:verbatim>Visualizar dados do processo seletivo<br />
		</div>
	</center>

		<c:if test="${not empty processosSeletivos}">
			
			<table class="listagem">
				<caption class="listagem">�ltimos Processos Seletivos</caption>
					 <c:set var="_edital" value=""/>
					<c:forEach items="#{processosSeletivos}" var="item"
						varStatus="status">
						
						<%-- SE PRIMEIRO LOOP IMPRIME O CABE�ALHO --%>
						<c:if test="${status.first}">
							<thead>
								<tr>
									<th><b>Curso</b></th>
									<th><b>N�vel</b></th>
									<th class="colVagas"><b>N� Vagas</b></th>
									<th class="colData"><b>Per�odo de Inscri��es</b></th>
									<th ></th>
								</tr>
							</thead>
							<tbody>
						</c:if>
						<c:if test="${not empty item.editalProcessoSeletivo && _edital != item.editalProcessoSeletivo.id}">
							<c:if test="${!status.first}">
							<tr>
								<td colspan="5" height="5px">
								</td>
							</tr>
							</c:if>
							<tr>
								<td colspan="4" class="agrupador">
									<b>
									<h:commandLink value="#{item.editalProcessoSeletivo.nome}" 
										title="#{item.editalProcessoSeletivo.descricaoPartePublica}" action="#{processoSeletivo.viewPublico}">
											<f:param name="id" value="#{item.id}" />
									</h:commandLink>
									</b>
								</td>
								<td  class="agrupador">&nbsp;	
									<c:if test="${processoSeletivo.processoStricto}">
										<a target="_blank" title="Acessar p�gina do programa" 
											href="${ctx}/public/programa/portal.jsf?lc=${portalPublicoPrograma.lc}&id=${item.curso.unidade.id}">
											<h:graphicImage url="#{ctx}/public/images/link.png" width="15px" height="15px" />
										</a>
									</c:if>
								</td>
							</tr>
						 </c:if>
						 <c:set var="_edital" value="${item.editalProcessoSeletivo.id}"/>
						
						<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O, AGRUPAR POR �REA  --%>
						<c:if test="${not empty item.matrizCurricular && not empty item.matrizCurricular.curso.areaVestibular.descricao && ultimaArea != item.matrizCurricular.curso.areaVestibular.descricao}">
							<tr>
								<td colspan="5"  class="agrupador">
									${item.matrizCurricular.curso.areaVestibular.descricao}
								</td>
							</tr>	
						</c:if>
					

						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<c:choose>
								<%-- SE PROCESSO SELETIVO CURSO LATOS, P�S E T�CNICO --%>
								<c:when test="${not empty item.curso}">
									<td>${item.curso.descricao}</td>
										<td class="nivel">${item.curso.nivelDescricao}</td>	
								</c:when>
								<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O --%>
								<c:otherwise>
									<td>${item.matrizCurricular.descricao}</td>
										<td class="nivel">${item.matrizCurricular.curso.nivelDescricao}</td>	
								</c:otherwise>
							</c:choose>			
						
							<td class="colVagas">
								<h:outputText value="#{item.vaga}" rendered="#{not empty item.vaga && item.vaga>0}" />
								<h:outputText value="N�o informado" rendered="#{empty item.vaga || item.vaga==0}" />
							</td>					
							<td nowrap="nowrap" class="colData periodo ${!item.inscricoesAbertas ? 'fechado' : ''} }">
								<ufrn:format type="data" valor="${item.editalProcessoSeletivo.inicioInscricoes}" /> 
								a <ufrn:format type="data" valor="${item.editalProcessoSeletivo.fimInscricoes}" />
							</td>
							<td width="16">
								<h:commandLink title="#{item.editalProcessoSeletivo.linkPartePublica}" immediate="true" 
									action="#{processoSeletivo.viewPublico}">
									<h:graphicImage url="/img/seta.gif" alt="#{ item.editalProcessoSeletivo.linkPartePublica }"/>
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>	
							</td>
						</tr>
						<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O --%>
						<c:if test="${not empty item.matrizCurricular}">
							<c:set var="ultimaArea" value="${item.matrizCurricular.curso.areaVestibular.descricao}"/>
						</c:if>
					</c:forEach>
			</table>
		</c:if>

		<c:if test="${empty processosSeletivos}">
			<style>
			.aviso {
				margin: 10px 20% 0;
				padding: 8px 10px;
				border: 1px solid #AAA;
				background: #EEE;
				color: #922;
				text-align: center;
				fon
			}
			</style>
		
				<div class="aviso">
					<p>Nenhum processo seletivo encontra-se aberto para inscri��es
					neste n�vel de ensino.</p>
					</div>
		</c:if>

	</h:form>
</f:view>

<%@include file="/public/include/voltar.jsp"%>
<%@include file="/public/include/rodape.jsp"%>