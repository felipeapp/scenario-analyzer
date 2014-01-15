		 <caption>Dados Básicos do Curso</caption>
		  <tr>
			 <th width="30%">Nome:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.nome}"/></td> 
			</tr>
			<tr>
	 		 <th>Unidade Responsável:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.unidade.nome}"/></td>
			</tr>
			<c:if test="${not empty cursoLatoMBean.obj.unidadesCursoLato}"> 
			   <tr>
			   	<th>Outras Unidades Envolvidas:</th>
			   	<td>
			   	    <c:forEach items="#{cursoLatoMBean.obj.unidadesCursoLato}" var="und" varStatus="status">
		             <tr>
		   				 <th></th>
		   				 <td>${und.unidade.nome} - ${und.unidade.municipio.nome}</td>
			   	   	 </tr>
			   	    </c:forEach>
			   	</td>
			   </tr>
			</c:if>
			<tr>
			 <th>Tipo do Curso:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.tipoCursoLato.descricao}"/></td>
			</tr>
			<tr>
			 <th>Modalidade Educação:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.modalidadeEducacao.descricao}"/></td>
			</tr>
			<c:if test="${not empty cursoLatoMBean.obj.polosCursos}"> 
			   <tr>
			   	<th>Polos:</th>
			   	<td>
			   	    <c:forEach items="#{cursoLatoMBean.obj.polosCursos}" var="polos" varStatus="status">
		             <tr>
		   				 <th class="linhaPar"></th>
		   				 <td>${polos.polo.cidade}</td>
			   	   	 </tr>
			   	    </c:forEach>
			   	</td>
			   </tr>
			</c:if>
			<tr>
			 <th>Carga Horária:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.cargaHoraria}"/></td>
			</tr>
			<tr>
			 <th>Número do Vagas:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.numeroVagas}"/></td>
			</tr>
			<tr>
			 <th>Vagas Servidores Internos:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.numeroVagasServidores}"/></td>
			</tr>
			<tr>
			 <th>Grande Área:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.grandeAreaConhecimentoCnpq.nome}"/></td>
			</tr>
			<tr>
			 <th>Área:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.areaConhecimentoCnpq.nome}"/></td>
			</tr>
			<tr>
			 <th>Sub-Área:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.subAreaConhecimentoCnpq.nome}"/></td>
			</tr>
			<tr>
			 <th>Especialidade:</th>
			 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.especialidadeAreaConhecimentoCnpq.nome}"/></td>
			</tr>
			<tr>
			 <th>Tipo do Trabalho de Conclusão:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.tipoTrabalhoConclusao.descricao}"/></td>
			</tr>
			<tr>
			 <th>Banca Examinadora:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.bancaExaminadora == false ? 'Não' : 'Sim'}"/></td>
			</tr>
			<tr>
			 <th>Período do Curso:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.dataInicio}"/>&nbsp;a&nbsp;
			 <h:outputText value="#{cursoLatoMBean.obj.dataFim}"/></td>
			</tr>
			<tr>
			 <th>Público Alvo:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.propostaCurso.publicoAlvo}"/></td>
			</tr>
			<c:if test="${not empty cursoLatoMBean.obj.propostaCurso.idArquivo}">
				<tr>
				 <th>Arquivo:</th>
				 <td><a href="/shared/verArquivo?idArquivo=${cursoLatoMBean.obj.propostaCurso.idArquivo}&key=${ sf:generateArquivoKey(cursoLatoMBean.obj.propostaCurso.idArquivo) }" target="_blank">Clique aqui para baixar</a></td>
				</tr>
			</c:if>
			
			<table class="subFormulario" width="100%">
			  <caption>Dados Portaria</caption>
				<tr>
				 <th width="30%">Número Portaria:</th>
				 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.propostaCurso.numeroPortaria}"/></td> 
				</tr>
				<tr>
				 <th>Ano Portaria:</th>
				 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.propostaCurso.anoPortaria}"/></td>
				</tr>
				<tr>
				 <th>Data Portaria:</th>
				 <td colspan="3"><h:outputText value="#{cursoLatoMBean.obj.propostaCurso.dataPublicacaoPortaria}"/></td>
				</tr>
			</table>			