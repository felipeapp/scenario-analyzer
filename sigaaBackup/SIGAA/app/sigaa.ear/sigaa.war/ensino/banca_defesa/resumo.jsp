<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="membroBancaMBean"/>
	<a4j:keepAlive beanName="buscaBancaDefesaMBean"/>
	<a4j:keepAlive beanName="bancaDefesaMBean"/>
	<h2 class="title"><ufrn:subSistema/> &gt; Banca de Defesa &gt; Resumo</h2>
	
	<c:if test="${bancaDefesaMBean.cadastro}">
		<div class="descricaoOperacao">
			<p><b>Caro Usuário,</b></p>
			<br/>
			<p>Cetifique-se se os dados informados estão corretos antes de <b>Confirmar</b> o cadastro.</p>		
		</div>	
	</c:if>
	
	<h:form id="form">
		<c:set value="#{bancaDefesaMBean.obj.discente}" var="discente"/>
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;">Matrícula:</th>
				<td style="text-align: left;">${discente.matricula }</td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Discente: </th>
				<td style="text-align: left;"> ${discente.pessoa.nome } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Curso: </th>
				<td style="text-align: left;"> ${discente.curso.nomeCursoStricto } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;"> Status: </th>
				<td style="text-align: left;"> ${discente.statusString } </td>
			</tr>
			<tr>
				<td></td>
				<th style="text-align: right;">Tipo:</th>
				<td style="text-align: left;"> ${discente.tipoString } </td>
			</tr>
			<tr>
				<td></td>
				<th> Orientador: </th>
				<td>
					<h:outputText value="#{bancaDefesaMBean.obj.orientacaoAtividade.orientador.nome}" 
						rendered="#{not empty bancaDefesaMBean.obj.orientacaoAtividade}" id="outputTextOrientador"/>
					<h:outputText value="Não informado" rendered="#{empty bancaDefesaMBean.obj.orientacaoAtividade}" id="outputTextNaoInformado"/>
				</td>
			</tr>			
		</table>	
		<br/>
		<table class="visualizacao" width="85%">
			<caption class="formulario">Dados da Banca</caption>
			<tr>
				<th width="20%">Local: </th>
				<td> ${bancaDefesaMBean.obj.local }
				</td>
			</tr>
			<tr>
				<th>Data:</th>
				<td> <ufrn:format type="data" valor="${bancaDefesaMBean.obj.dataDefesa}" ></ufrn:format> </td>
			</tr>
			<tr>
				<th>Hora: </th>
				<td>
					<ufrn:format type="hora" valor="${bancaDefesaMBean.obj.dataDefesa}" ></ufrn:format>
				</td>
			</tr>		
			<c:if test="${bancaDefesaMBean.obj.matriculaComponente != null && bancaDefesaMBean.obj.matriculaComponente.id > 0}" >
				<tr>
					<th>Atividade:</th>
					<td>
						 ${bancaDefesaMBean.obj.matriculaComponente.componenteDescricao} (${bancaDefesaMBean.obj.matriculaComponente.anoPeriodo}) 
						 - ${bancaDefesaMBean.obj.matriculaComponente.situacaoMatricula.descricao}
					</td>
				</tr>
			</c:if>			
			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Dados do Trabalho</caption>
						<tr>
							<th width="20%" valign="top">Título:</th>
							<td>${bancaDefesaMBean.obj.titulo}
							</td>
						</tr>
						<tr>
							<th valign="top">Palavras chave:</th>
							<td>${bancaDefesaMBean.obj.palavrasChave}
							</td>
						</tr>						
						<tr>
							<th>Páginas:</th>
							<td>${bancaDefesaMBean.obj.paginas}
							</td>
						</tr>												
						<tr>
							<th>Grande Área:</th>
							<td>${bancaDefesaMBean.obj.area.grandeArea.nome} </td>
						</tr>
						<tr>
							<th>Área:</th>
							<td>${bancaDefesaMBean.obj.area.area.nome} </td>
						</tr>
						<c:if test="${bancaDefesaMBean.obj.subArea.id > 0}">
							<tr>
								<th>Sub-Área:</th>
								<td>${bancaDefesaMBean.obj.subArea.nome} </td>
							</tr>
						</c:if>						
						<tr>
							<th valign="top">Resumo:</th>
							<td>${bancaDefesaMBean.obj.resumo}
							</td>
						</tr>											
						<tr>
							<th valign="top">Observação:</th>
							<td>${bancaDefesaMBean.obj.observacao}
							</td>
						</tr>												
				</table>
				</td>
			</tr>

			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%;" >
					<caption>Membros da Banca</caption>
					<tbody>
					<c:forEach items="${bancaDefesaMBean.obj.membrosBanca}" var="membro" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td width="15%" >${membro.tipo.descricao}</td>
							<td align="left">${membro.membroIdentificacao }</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center">
						<h:commandButton id="confirmar" value="Confirmar " action="#{bancaDefesaMBean.confirmar}" rendered="#{bancaDefesaMBean.cadastro}"/>
						<h:commandButton id="dados" value="<< Dados Gerais" action="#{bancaDefesaMBean.telaDadosBanca}" rendered="#{bancaDefesaMBean.cadastro}"/>
						<h:commandButton id="membros" value="<< Membros da Banca" action="#{bancaDefesaMBean.telaMembros}" rendered="#{bancaDefesaMBean.cadastro}"/>
						<h:commandButton value="Cancelar" id="cancelamento" onclick="#{confirm}" action="#{bancaDefesaMBean.cancelar}" rendered="#{bancaDefesaMBean.cadastro}"/>
						<h:commandButton value="<< Voltar" id="btVoltar" action="#{bancaDefesaMBean.cancelar}" rendered="#{!bancaDefesaMBean.cadastro}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
